package com.raulespim.bankingaccounts.features.accounts.data.repository

import com.raulespim.bankingaccounts.features.accounts.data.local.AccountDao
import com.raulespim.bankingaccounts.features.accounts.data.mappers.toAccount
import com.raulespim.bankingaccounts.features.accounts.data.mappers.toAccountEntity
import com.raulespim.bankingaccounts.features.accounts.data.remote.AccountApi
import com.raulespim.bankingaccounts.features.accounts.domain.Account
import com.raulespim.bankingaccounts.features.accounts.domain.repository.AccountRepository
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
}