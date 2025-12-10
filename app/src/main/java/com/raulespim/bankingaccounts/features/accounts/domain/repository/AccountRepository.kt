package com.raulespim.bankingaccounts.features.accounts.domain.repository

import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.AccountDetailsDto
import com.raulespim.bankingaccounts.features.accounts.domain.model.Account
import com.raulespim.bankingaccounts.features.accounts.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<Result<List<Account>>>
    suspend fun refreshAccounts()
    fun getFavoriteAccountOffline(): Flow<Account?>
    suspend fun toggleFavorite(accountId: String, currentFavoriteId: String?): Result<Unit>
    suspend fun getAccountDetails(accountId: String): Result<AccountDetailsDto>
    suspend fun getTransactionsPage(
        accountId: String,
        nextPage: Int,
        fromDate: String?,
        toDate: String?
    ): Result<PagedTransactions>
}

data class PagedTransactions(
    val transactions: List<Transaction>,
    val currentPage: Int,
    val totalPages: Int
)