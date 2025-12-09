package com.raulespim.bankingaccounts.features.accounts.domain.model

import java.time.LocalDateTime

data class Account(
    val id: String,
    val accountNumber: Long,
    val balance: String,
    val currencyCode: String,
    val accountType: String,
    val accountNickname: String?,
    val productName: String? = null,
    val openedDate: String? = null,
    val branch: String? = null,
    val beneficiaries: List<String> = emptyList(),
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = accountNickname?.takeIf { it.isNotBlank() } ?: accountNumber.toString()
}
