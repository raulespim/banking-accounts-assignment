package com.raulespim.bankingaccounts.features.accounts.domain.usecase

import com.raulespim.bankingaccounts.features.accounts.domain.repository.AccountRepository
import javax.inject.Inject

class SetFavoriteAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {

}