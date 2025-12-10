package com.raulespim.bankingaccounts.features.accounts.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulespim.bankingaccounts.features.accounts.domain.model.Account
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
        refreshAccounts(silent = true)
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            getAccountsUseCase()
                .onStart {
                    _uiState.value = AccountsUiState.Loading
                }
                .catch { throwable ->
                    _uiState.value = AccountsUiState.Error("Unexpected error")
                }
                .collect { result ->
                    val accounts = result.getOrNull().orEmpty()

                    _uiState.value = when {
                        accounts.isNotEmpty() -> {
                            AccountsUiState.Success(
                                accounts.sortedByDescending { it.isFavorite }
                            )
                        }
                        result.isFailure -> {
                            AccountsUiState.Error("No internet connection")
                        }
                        else -> {
                            AccountsUiState.Success(emptyList())
                        }
                    }
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