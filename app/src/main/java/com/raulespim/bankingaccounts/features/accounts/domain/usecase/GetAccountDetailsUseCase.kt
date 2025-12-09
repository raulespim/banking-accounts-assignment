package com.raulespim.bankingaccounts.features.accounts.domain.usecase

import com.raulespim.bankingaccounts.features.accounts.domain.repository.AccountRepository
import javax.inject.Inject

class GetAccountDetailsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(accountId: String) = repository.getAccountDetails(accountId)
}