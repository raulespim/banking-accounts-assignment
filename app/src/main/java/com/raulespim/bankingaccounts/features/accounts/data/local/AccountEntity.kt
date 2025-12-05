package com.raulespim.bankingaccounts.features.accounts.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AccountEntity")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val accountNumber: Long,
    val balance: String,
    val currencyCode: String,
    val accountType: String,
    val accountNickname: String?,
    val isFavorite: Boolean = false
)
