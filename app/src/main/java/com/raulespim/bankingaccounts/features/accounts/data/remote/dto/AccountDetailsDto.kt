package com.raulespim.bankingaccounts.features.accounts.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountDetailsDto(
    val product_name: String,
    val opened_date: String,
    val branch: String,
    val beneficiaries: List<String>
)
