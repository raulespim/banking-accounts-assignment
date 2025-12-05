package com.raulespim.bankingaccounts.features.accounts.data.remote

import retrofit2.http.GET

interface AccountApi {

    @GET("accounts")
    suspend fun getAccounts(): List<AccountDto>
}