package com.raulespim.bankingaccounts.features.accounts.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionsResponse(
    val transactions: List<TransactionDto>,
    val paging: PagingDto
)

@Serializable
data class PagingDto(
    val pages_count: Int,
    val total_items: Int,
    val current_page: Int
)

@Serializable
data class TransactionDto(
    val id: String,
    val date: String,
    val transaction_amount: String,
    val transaction_type: String,
    val description: String?,
    val is_debit: Boolean
)

@Serializable
data class TransactionsRequestBody(
    val next_page: Int,
    val from_date: String? = null,
    val to_date: String? = null
)