package com.raulespim.bankingaccounts.features.accounts.domain

data class Account(
    val id: String,
    val accountNumber: Long,
    val balance: String,
    val currencyCode: String,
    val accountType: String,
    val accountNickname: String?,
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = accountNickname?.takeIf { it.isNotBlank() } ?: accountNumber.toString()
}
