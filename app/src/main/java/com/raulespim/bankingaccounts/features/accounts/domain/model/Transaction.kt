package com.raulespim.bankingaccounts.features.accounts.domain.model

import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.TransactionDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Transaction(
    val id: String,
    val date: LocalDateTime,
    val description: String?,
    val amount: String,
    val type: String,
    val currencyCode: String = "EUR",
    val isDebit: Boolean
) {
    companion object {
        private val isoFormatter = DateTimeFormatter.ISO_DATE_TIME
        fun fromDto(dto: TransactionDto): Transaction = Transaction(
            id = dto.id,
            date = LocalDateTime.parse(dto.date, isoFormatter),
            description = dto.description,
            amount = dto.transaction_amount,
            type = dto.transaction_type,
            isDebit = dto.is_debit
        )
    }

    val signedAmount: String get() = if (isDebit) "-$amount" else "+$amount"
}
