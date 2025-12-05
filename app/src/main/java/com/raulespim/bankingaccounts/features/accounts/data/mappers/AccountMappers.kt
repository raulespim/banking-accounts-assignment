package com.raulespim.bankingaccounts.features.accounts.data.mappers

import com.raulespim.bankingaccounts.features.accounts.data.local.AccountEntity
import com.raulespim.bankingaccounts.features.accounts.data.remote.AccountDto
import com.raulespim.bankingaccounts.features.accounts.domain.Account

fun AccountDto.toAccountEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        accountNumber = account_number,
        balance = balance,
        currencyCode = currency_code,
        accountType = account_type,
        accountNickname = account_nickname,
        isFavorite = false
    )
}

fun AccountEntity.toAccount(): Account {
    return Account(
        id = id,
        accountNumber = accountNumber,
        balance = balance,
        currencyCode = currencyCode,
        accountType = accountType,
        accountNickname = accountNickname,
        isFavorite = isFavorite
    )
}

fun Account.toAccountEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        accountNumber = accountNumber,
        balance = balance,
        currencyCode = currencyCode,
        accountType = accountType,
        accountNickname = accountNickname,
        isFavorite = isFavorite
    )
}