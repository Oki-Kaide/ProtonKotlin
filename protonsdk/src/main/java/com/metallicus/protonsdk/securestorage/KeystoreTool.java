/*
 * Copyright (C) 2017 adorsys GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Modifications Copyright (C) 2021 Proton Chain LLC, Delaware
 *
 * Modifications include changes to KeyStore alias
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.metallicus.protonsdk.securestorage;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.metallicus.protonsdk.BuildConfig;
import com.metallicus.protonsdk.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.P;
import static com.metallicus.protonsdk.securestorage.SecureStorageException.ExceptionType.CRYPTO_EXCEPTION;
import static com.metallicus.protonsdk.securestorage.SecureStorageException.ExceptionType.INTERNAL_LIBRARY_EXCEPTION;
import static com.metallicus.protonsdk.securestorage.SecureStorageException.ExceptionType.KEYSTORE_EXCEPTION;

final class KeystoreTool {
	private static final String KEY_ALIAS = "ProtonKeyPair";
	private static final String KEY_ENCRYPTION_ALGORITHM = "RSA";
	private static final String KEY_CHARSET = "UTF-8";
	private static final String KEY_KEYSTORE_NAME = "AndroidKeyStore";
	private static final String KEY_CIPHER_JELLYBEAN_PROVIDER = "AndroidOpenSSL";
	private static final String KEY_CIPHER_MARSHMALLOW_PROVIDER = "AndroidKeyStoreBCWorkaround";
	private static final String KEY_TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding";

	// hidden constructor to disable initialization
	private KeystoreTool() {
	}

	@Nullable
	static String encryptMessage(@NonNull Context context, @NonNull String plainMessage) throws SecureStorageException {
		try {
			Cipher input;
			if (VERSION.SDK_INT >= M) {
				input = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_MARSHMALLOW_PROVIDER);
			} else {
				input = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_JELLYBEAN_PROVIDER);
			}
			input.init(Cipher.ENCRYPT_MODE, getPublicKey(context));

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			CipherOutputStream cipherOutputStream = new CipherOutputStream(
				outputStream, input);
			cipherOutputStream.write(plainMessage.getBytes(KEY_CHARSET));
			cipherOutputStream.close();

			byte[] values = outputStream.toByteArray();
			return Base64.encodeToString(values, Base64.DEFAULT);

		} catch (Exception e) {
			throw new SecureStorageException(e.getMessage(), e, KEYSTORE_EXCEPTION);
		}
	}

	@NonNull
	static String decryptMessage(@NonNull Context context, @NonNull String encryptedMessage) throws SecureStorageException {
		try {
			Cipher output;
			if (VERSION.SDK_INT >= M) {
				output = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_MARSHMALLOW_PROVIDER);
			} else {
				output = Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_JELLYBEAN_PROVIDER);
			}
			output.init(Cipher.DECRYPT_MODE, getPrivateKey(context));

			CipherInputStream cipherInputStream = new CipherInputStream(
				new ByteArrayInputStream(Base64.decode(encryptedMessage, Base64.DEFAULT)), output);
			List<Byte> values = new ArrayList<>();

			int nextByte;
			while ((nextByte = cipherInputStream.read()) != -1) { //NOPMD
				values.add((byte) nextByte);
			}

			byte[] bytes = new byte[values.size()];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = values.get(i);
			}

			return new String(bytes, 0, bytes.length, KEY_CHARSET);

		} catch (Exception e) {
			throw new SecureStorageException(e.getMessage(), e, CRYPTO_EXCEPTION);
		}
	}

	static boolean keyPairExists() {// throws SecureStorageException {
		boolean keyExists;
		try {
			keyExists = getKeyStoreInstance().getKey(KEY_ALIAS, null) != null;
		} catch (Exception e) {
			keyExists = false;
//            throw new SecureStorageException(e.getMessage(), e, KEYSTORE_EXCEPTION);
		}
		return keyExists;
	}

	static void generateKeyPair(@NonNull Context context) throws SecureStorageException {
		if (isRTL(context)) {
			Locale.setDefault(Locale.US);
		}

		if (VERSION.SDK_INT >= M) {
			generateMarshmallowKeyPair();
		} else {
			PRNGFixes.apply();
			generateDeprecatedKeyPair(context);
		}
	}

	@RequiresApi(api = M)
	private static void generateMarshmallowKeyPair() throws SecureStorageException {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ENCRYPTION_ALGORITHM, KEY_KEYSTORE_NAME);

			KeyGenParameterSpec keyGenParameterSpec =
				new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
					.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
					.build();

			generator.initialize(keyGenParameterSpec);
			generator.generateKeyPair();
		} catch (Exception e) {
			throw new SecureStorageException(e.getMessage(), e, KEYSTORE_EXCEPTION);
		}
	}

	@SuppressWarnings("deprecation")
	private static void generateDeprecatedKeyPair(@NonNull Context context) throws SecureStorageException {
		try {
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			end.add(Calendar.YEAR, 99);

			android.security.KeyPairGeneratorSpec spec = new android.security.KeyPairGeneratorSpec.Builder(context)
				.setAlias(KEY_ALIAS)
				.setSubject(new X500Principal("CN=" + KEY_ALIAS))
				.setSerialNumber(BigInteger.TEN)
				.setStartDate(start.getTime())
				.setEndDate(end.getTime())
				.build();

			KeyPairGenerator generator
				= KeyPairGenerator.getInstance(KEY_ENCRYPTION_ALGORITHM, KEY_KEYSTORE_NAME);
			generator.initialize(spec);
			generator.generateKeyPair();
		} catch (Exception e) {
			throw new SecureStorageException(e.getMessage(), e, KEYSTORE_EXCEPTION);
		}
	}

	static void deleteKeyPair() throws SecureStorageException {
		// Delete Key from Keystore
//        if (keyPairExists()) {
		try {
			getKeyStoreInstance().deleteEntry(KEY_ALIAS);
		} catch (KeyStoreException e) {
			throw new SecureStorageException(e.getMessage(), e, KEYSTORE_EXCEPTION);
		}
//        } else if (BuildConfig.DEBUG) {
//            Log.e(KeystoreTool.class.getName(),
//                context.get().getString(R.string.message_keypair_does_not_exist));
//        }
	}

	@Nullable
	private static PublicKey getPublicKey(@NonNull Context context) throws SecureStorageException {
		PublicKey publicKey;
		try {
			if (keyPairExists()) {
				if (VERSION.SDK_INT >= P) {
					// only for P and newer versions
					publicKey = getKeyStoreInstance().getCertificate(KEY_ALIAS).getPublicKey();
				} else {
					KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStoreInstance().getEntry(KEY_ALIAS, null);
					publicKey = privateKeyEntry.getCertificate().getPublicKey();
				}
			} else {
				if (BuildConfig.DEBUG) {
					Log.e(KeystoreTool.class.getName(), context.getString(R.string.secureStorageKeyPairDoesNotExist));
				}
				throw new SecureStorageException(context.getString(R.string.secureStorageKeyPairDoesNotExist), null, INTERNAL_LIBRARY_EXCEPTION);
			}
		} catch (Exception e) {
			throw new SecureStorageException(e.getMessage(), e, KEYSTORE_EXCEPTION);
		}

		return publicKey;
	}

	@Nullable
	private static PrivateKey getPrivateKey(@NonNull Context context) throws SecureStorageException {
		PrivateKey privateKey;
		try {
			if (keyPairExists()) {
				if (VERSION.SDK_INT >= P) {
					// only for P and newer versions
					privateKey = (PrivateKey) getKeyStoreInstance().getKey(KEY_ALIAS, null);
				} else {
					KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStoreInstance().getEntry(KEY_ALIAS, null);
					privateKey = privateKeyEntry.getPrivateKey();
				}
			} else {
				if (BuildConfig.DEBUG) {
					Log.e(KeystoreTool.class.getName(), context.getString(R.string.secureStorageKeyPairDoesNotExist));
				}
				throw new SecureStorageException(context.getString(R.string.secureStorageKeyPairDoesNotExist), null, INTERNAL_LIBRARY_EXCEPTION);
			}
		} catch (Exception e) {
			throw new SecureStorageException(e.getMessage(), e, KEYSTORE_EXCEPTION);
		}
		return privateKey;
	}

	private static boolean isRTL(@NonNull Context context) {
		Configuration config = context.getResources().getConfiguration();
		return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
	}

	@NonNull
	private static KeyStore getKeyStoreInstance() throws SecureStorageException {
		try {
			// Get the AndroidKeyStore instance
			KeyStore keyStore = KeyStore.getInstance(KEY_KEYSTORE_NAME);

			// Relict of the JCA API - you have to call load even
			// if you do not have an input stream you want to load or it'll crash
			keyStore.load(null);

			return keyStore;
		} catch (Exception e) {
			throw new SecureStorageException(e.getMessage(), e, KEYSTORE_EXCEPTION);
		}
	}
}