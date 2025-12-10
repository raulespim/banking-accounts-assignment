package com.raulespim.bankingaccounts.provide

import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.AccountDetailsDto
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.PagingDto
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.TransactionDto
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.TransactionsResponse
import com.raulespim.bankingaccounts.features.accounts.domain.model.Account
import com.raulespim.bankingaccounts.features.accounts.presentation.details.AccountDetailsViewModelTest.Companion.TEST_ACCOUNT_ID

// ===================================================================
// list screen
// ===================================================================

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

// ===================================================================
// details screen
// ===================================================================

val testAccountMock = Account(
    id = TEST_ACCOUNT_ID,
    accountNumber = 12345,
    balance = "99.00",
    currencyCode = "EUR",
    accountType = "current",
    accountNickname = "My Salary",
    isFavorite = false,
    productName = null,
    openedDate = null,
    branch = null,
)

val testAccountDetailsDtoMock = AccountDetailsDto(
    product_name = "Current Account EUR",
    opened_date = "2015-12-03T10:15:30Z",
    branch = "Main Branch",
    beneficiaries = listOf("John Doe")
)

val transactionDto1Mock = TransactionDto(
    id = "tx001",
    date = "2025-03-20T14:30:00Z",
    transaction_amount = "45.50",
    transaction_type = "purchase",
    description = "Coffee Shop",
    is_debit = true
)

val transactionDto2Mock = TransactionDto(
    id = "tx002",
    date = "2025-03-05T09:00:00Z",
    transaction_amount = "2500.00",
    transaction_type = "income",
    description = "Salary March",
    is_debit = false
)

val transactionDto3Mock = TransactionDto(
    id = "tx003",
    date = "2025-02-28T18:20:00Z",
    transaction_amount = "120.00",
    transaction_type = "purchase",
    description = "Shopping",
    is_debit = true
)

val testTransactionsPage1Mock = TransactionsResponse(
    transactions = listOf(transactionDto1Mock, transactionDto2Mock),
    paging = PagingDto(
        pages_count = 3,
        total_items = 45,
        current_page = 0
    )
)

val testTransactionsPage2Mock = TransactionsResponse(
    transactions = listOf(transactionDto3Mock),
    paging = PagingDto(
        pages_count = 3,
        total_items = 45,
        current_page = 1
    )
)