package com.raulespim.bankingaccounts.provide

import com.raulespim.bankingaccounts.features.accounts.domain.model.Account

val regularAccountMock = Account(
    id = "1",
    accountNumber = 54321,
    balance = "2316.00",
    currencyCode = "GBP",
    accountType = "current",
    accountNickname = null,
    isFavorite = false
)

val favoriteAccountMock = Account(
    id = "2",
    accountNumber = 12345,
    balance = "99.00",
    currencyCode = "EUR",
    accountType = "current",
    accountNickname = "My Salary",
    isFavorite = true
)

val emptyAccountList = emptyList<Account>()