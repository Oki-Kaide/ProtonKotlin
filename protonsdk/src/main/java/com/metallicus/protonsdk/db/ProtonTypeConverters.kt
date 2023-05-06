/*
 * Copyright (c) 2021 Proton Chain LLC, Delaware
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
package com.metallicus.protonsdk.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.metallicus.protonsdk.model.*

object ProtonTypeConverters {
	@TypeConverter
	@JvmStatic
	fun stringToAccountContact(value: String?): AccountContact? {
		val type = object : TypeToken<AccountContact>() {}.type
		return Gson().fromJson(value, type)
	}

	@TypeConverter
	@JvmStatic
	fun accountContactToString(accountContact: AccountContact): String {
		val type = object : TypeToken<AccountContact>() {}.type
		return Gson().toJson(accountContact, type)
	}

	@TypeConverter
	@JvmStatic
	fun stringToAccountVotersXPRInfo(value: String?): AccountVotersXPRInfo? {
		val type = object : TypeToken<AccountVotersXPRInfo>() {}.type
		return Gson().fromJson(value, type)
	}

	@TypeConverter
	@JvmStatic
	fun accountVotersXPRInfoToString(accountVotersXPRInfo: AccountVotersXPRInfo): String {
		val type = object : TypeToken<AccountVotersXPRInfo>() {}.type
		return Gson().toJson(accountVotersXPRInfo, type)
	}

	@TypeConverter
	@JvmStatic
	fun stringToAccountRefundsXPRInfo(value: String?): AccountRefundsXPRInfo? {
		val type = object : TypeToken<AccountRefundsXPRInfo>() {}.type
		return Gson().fromJson(value, type)
	}

	@TypeConverter
	@JvmStatic
	fun accountRefundsXPRInfoToString(accountRefundsXPRInfo: AccountRefundsXPRInfo): String {
		val type = object : TypeToken<AccountRefundsXPRInfo>() {}.type
		return Gson().toJson(accountRefundsXPRInfo, type)
	}

	@TypeConverter
	@JvmStatic
	fun stringToKYCProviderList(value: String): List<KYCProvider> {
		val type = object : TypeToken<List<KYCProvider>>() {}.type
		return Gson().fromJson(value, type)
	}

	@TypeConverter
	@JvmStatic
	fun kycProviderListToString(value: List<KYCProvider>): String {
		val type = object : TypeToken<List<KYCProvider>>() {}.type
		return Gson().toJson(value, type)
	}

	@TypeConverter
	@JvmStatic
	fun stringToChainUrlInfoList(value: String): List<ChainUrlInfo> {
		val type = object : TypeToken<List<ChainUrlInfo>>() {}.type
		return Gson().fromJson(value, type)
	}

	@TypeConverter
	@JvmStatic
	fun chainUrlInfoListToString(value: List<ChainUrlInfo>): String {
		val type = object : TypeToken<List<ChainUrlInfo>>() {}.type
		return Gson().toJson(value, type)
	}

	@TypeConverter
	@JvmStatic
	fun stringToStringTokenContractRateMap(value: String?): Map<String, TokenContractRate>? {
		val type = object : TypeToken<Map<String, TokenContractRate>>() {}.type
		return Gson().fromJson(value, type)
	}

	@TypeConverter
	@JvmStatic
	fun stringTokenContractRateMapToString(map: Map<String, TokenContractRate>): String {
		val type = object : TypeToken<Map<String, TokenContractRate>>() {}.type
		return Gson().toJson(map, type)
	}
}