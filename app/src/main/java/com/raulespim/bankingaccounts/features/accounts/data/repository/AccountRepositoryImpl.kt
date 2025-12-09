package com.raulespim.bankingaccounts.features.accounts.data.repository

import com.raulespim.bankingaccounts.features.accounts.data.local.dao.AccountDao
import com.raulespim.bankingaccounts.features.accounts.data.mappers.toAccount
import com.raulespim.bankingaccounts.features.accounts.data.mappers.toAccountEntity
import com.raulespim.bankingaccounts.features.accounts.data.remote.AccountApi
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.AccountDetailsDto
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.TransactionsRequestBody
import com.raulespim.bankingaccounts.features.accounts.domain.model.Account
import com.raulespim.bankingaccounts.features.accounts.domain.model.Transaction
import com.raulespim.bankingaccounts.features.accounts.domain.repository.AccountRepository
import com.raulespim.bankingaccounts.features.accounts.domain.repository.PagedTransactions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val api: AccountApi,
    private val dao: AccountDao
) : AccountRepository {

    override fun getAccounts(): Flow<Result<List<Account>>> = dao
        .getAllAccountsOrdered()
        .map { entities -> Result.success(entities.map { it.toAccount() }) }
        .catch { emit(Result.failure(it)) }

    override suspend fun refreshAccounts() {
        try {
            val remoteAccounts = api.getAccounts()
            dao.clearAll()
            dao.upsertAll(remoteAccounts.map { it.toAccountEntity() })
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun setFavoriteAccount(accountId: String?) {
        if (accountId == null) {
            dao.clearFavorite()
        } else {
            dao.setFavorite(accountId)
        }
    }

    override fun getFavoriteAccountOffline(): Flow<Account?> =
        dao.getFavoriteAccount().map { it?.toAccount() }

    override suspend fun toggleFavorite(
        accountId: String,
        currentFavoriteId: String?
    ): Result<Unit> = runCatching {
        if (currentFavoriteId == accountId) {
            dao.clearFavorite()
        } else {
            dao.setFavorite(accountId)
        }
    }

    override suspend fun getAccountDetails(accountId: String): Result<AccountDetailsDto> =
        runCatching { api.getAccountDetails(accountId) }

    override suspend fun getTransactionsPage(
        accountId: String,
        nextPage: Int,
        fromDate: String?,
        toDate: String?
    ): Result<PagedTransactions> = runCatching {
        val body = TransactionsRequestBody(next_page = nextPage, from_date = fromDate, to_date = toDate)
        val resp = api.getTransactions(accountId, body)
        PagedTransactions(
            transactions = resp.transactions.map { Transaction.fromDto(it) },
            currentPage = resp.paging.current_page,
            totalPages = resp.paging.pages_count
        )
    }
}