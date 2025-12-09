package com.raulespim.bankingaccounts.features.accounts.data.remote

import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.AccountDetailsDto
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.AccountDto
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.TransactionsRequestBody
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.TransactionsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AccountApi {

    @GET("accounts")
    suspend fun getAccounts(): List<AccountDto>

    @GET("account/details/{account_id}")
    suspend fun getAccountDetails(
        @Path("account_id") accountId: String
    ): AccountDetailsDto

    @POST("account/transactions/{account_id}")
    suspend fun getTransactions(
        @Path("account_id") accountId: String,
        @Body body: TransactionsRequestBody
    ): TransactionsResponse
}