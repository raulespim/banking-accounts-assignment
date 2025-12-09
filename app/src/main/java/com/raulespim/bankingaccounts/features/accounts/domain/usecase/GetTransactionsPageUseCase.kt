package com.raulespim.bankingaccounts.features.accounts.domain.usecase

import com.raulespim.bankingaccounts.features.accounts.domain.repository.AccountRepository
import javax.inject.Inject

class GetTransactionsPageUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(
        accountId: String,
        page: Int,
        from: String?,
        to: String?
    ) = repository.getTransactionsPage(accountId, page, from, to)
}