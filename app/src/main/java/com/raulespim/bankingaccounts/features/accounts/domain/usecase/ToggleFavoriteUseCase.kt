package com.raulespim.bankingaccounts.features.accounts.domain.usecase

import com.raulespim.bankingaccounts.features.accounts.domain.repository.AccountRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(accountId: String, currentFavoriteId: String?) =
        repository.toggleFavorite(accountId, currentFavoriteId)
}