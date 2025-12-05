package com.raulespim.bankingaccounts.features.accounts.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: String,
    val account_number: Long,
    val balance: String,
    val currency_code: String,
    val account_type: String,
    val account_nickname: String?
)
