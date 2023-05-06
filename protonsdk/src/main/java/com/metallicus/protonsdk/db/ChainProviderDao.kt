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

import androidx.room.*
import com.metallicus.protonsdk.model.ChainProvider

/**
 * Interface for database access for [ChainProvider] related operations
 */
@Dao
interface ChainProviderDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(chainProvider: ChainProvider)

	@Update
	suspend fun update(chainProvider: ChainProvider)

	@Query("SELECT * FROM chainProvider WHERE chainId = :id")
	suspend fun findById(id: String): ChainProvider

	@Query("SELECT * FROM chainProvider")
	suspend fun findAll(): List<ChainProvider>

	@Query("UPDATE chainProvider SET chainUrl = :chainUrl WHERE chainId = :chainId")
	suspend fun updateChainUrl(chainId: String, chainUrl: String)

	@Query("UPDATE chainProvider SET hyperionHistoryUrl = :hyperionHistoryUrl WHERE chainId = :chainId")
	suspend fun updateHyperionHistoryUrl(chainId: String, hyperionHistoryUrl: String)

	@Query("DELETE FROM chainProvider")
	suspend fun removeAll()
}