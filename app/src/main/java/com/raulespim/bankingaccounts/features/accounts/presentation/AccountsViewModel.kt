package com.raulespim.bankingaccounts.features.accounts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulespim.bankingaccounts.features.accounts.domain.Account
import com.raulespim.bankingaccounts.features.accounts.domain.repository.AccountRepository
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetAccountsUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.RefreshAccountsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AccountsUiState {
    data object Loading : AccountsUiState
    data class Success(val accounts: List<Account>) : AccountsUiState
    data class Error(val message: String) : AccountsUiState
}

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val refreshAccountsUseCase: RefreshAccountsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountsUiState>(AccountsUiState.Loading)
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        observeAccounts()
        refreshAccounts(silent = false)
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            getAccountsUseCase()
                .map { result ->
                    result.fold(
                        onSuccess = { AccountsUiState.Success(it) },
                        onFailure = { AccountsUiState.Error(it.message ?: "Unknown error") }
                    )
                }
                .onStart { emit(AccountsUiState.Loading) }
                .catch { emit(AccountsUiState.Error("Unexpected error")) }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun refreshAccounts(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _uiState.value = AccountsUiState.Loading
            }

            refreshAccountsUseCase()
                .onFailure { exception ->
                    _uiState.value = AccountsUiState.Error(
                        exception.message ?: "Check your connection"
                    )
                }
        }
    }
}