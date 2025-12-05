package com.raulespim.bankingaccounts.features.accounts.domain.usecase

import com.raulespim.bankingaccounts.features.accounts.domain.repository.AccountRepository
import javax.inject.Inject

class GetFavoriteOfflineUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke() = repository.getFavoriteAccountOffline()
}