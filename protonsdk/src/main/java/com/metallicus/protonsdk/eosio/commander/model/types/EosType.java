/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
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
package com.metallicus.protonsdk.eosio.commander.model.types;

import java.util.Collection;

/**
 * Created by swapnibble on 2017-09-12.
 */

public interface EosType {
	class InsufficientBytesException extends Exception {

		private static final long serialVersionUID = 1L;
	}

	interface Packer {
		void pack(Writer writer);
	}

	interface Unpacker {
		void unpack(Reader reader) throws InsufficientBytesException;
	}

	interface Reader {
		byte get() throws InsufficientBytesException;

		int getShortLE() throws InsufficientBytesException;

		int getIntLE() throws InsufficientBytesException;

		long getLongLE() throws InsufficientBytesException;

		byte[] getBytes(int size) throws InsufficientBytesException;

		String getString() throws InsufficientBytesException;

		long getVariableUint() throws InsufficientBytesException;
	}

	interface Writer {
		void put(byte b);

		void putShortLE(short value);

		void putIntLE(int value);

		void putLongLE(long value);

		void putBytes(byte[] value);

		void putString(String value);

		byte[] toBytes();

		int length();

		void putCollection(Collection<? extends Packer> collection);

		void putVariableUInt(long val);
	}
}
