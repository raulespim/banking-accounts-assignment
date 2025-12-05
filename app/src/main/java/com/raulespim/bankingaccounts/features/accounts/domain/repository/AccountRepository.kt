package com.raulespim.bankingaccounts.features.accounts.domain.repository

import com.raulespim.bankingaccounts.features.accounts.domain.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<Result<List<Account>>>
    suspend fun refreshAccounts()
    suspend fun setFavoriteAccount(accountId: String?)
    fun getFavoriteAccountOffline(): Flow<Account?>
}