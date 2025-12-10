package com.raulespim.bankingaccounts.features.accounts.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulespim.bankingaccounts.features.accounts.domain.model.Account
import com.raulespim.bankingaccounts.features.accounts.domain.model.Transaction
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetAccountDetailsUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetAccountsUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetFavoriteOfflineUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetTransactionsPageUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class TransactionSection(val monthYear: String, val items: List<Transaction>)

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Success(
        val account: Account,
        val sections: List<TransactionSection>,
        val hasMore: Boolean,
        val isFiltering: Boolean
    ) : DetailsUiState
    data class Error(val msg: String, val showRetry: Boolean = true) : DetailsUiState
}

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase,
    private val getTransactionsPageUseCase: GetTransactionsPageUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoriteOfflineUseCase: GetFavoriteOfflineUseCase
) : ViewModel() {

    private val accountId: String = savedStateHandle["accountId"]!!

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var currentPage = 0
    private var fromDate: String? = null
    private var toDate: String? = null

    init {
        observeFavoriteStatus()
        loadEverything(refresh = true)
    }

    private fun observeFavoriteStatus() {
        getFavoriteOfflineUseCase()
            .onEach { favoriteAccount ->
                val isFavorite = favoriteAccount?.id == accountId
                _uiState.update { current ->
                    if (current is DetailsUiState.Success) {
                        current.copy(account = current.account.copy(isFavorite = isFavorite))
                    } else current
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadEverything(refresh: Boolean) {

        if (refresh) {
            currentPage = 0
            _uiState.value = DetailsUiState.Loading
        }

        viewModelScope.launch {

            val cachedAccount = getAccountsUseCase()
                .first()
                .getOrNull()
                ?.find { it.id == accountId }
                ?: return@launch _uiState.emit(DetailsUiState.Error("Account not found"))

            val detailsResult = getAccountDetailsUseCase(accountId)
            val enrichedAccount = detailsResult.getOrNull()?.let { dto ->
                cachedAccount.copy(
                    productName = dto.product_name,
                    openedDate = dto.opened_date,
                    branch = dto.branch,
                    beneficiaries = dto.beneficiaries
                )
            } ?: cachedAccount

            val currentFavoriteId = getFavoriteOfflineUseCase().first()?.id
            val accountWithFavorite = enrichedAccount.copy(
                isFavorite = enrichedAccount.id == currentFavoriteId
            )

            val txResult = getTransactionsPageUseCase(accountId, currentPage, fromDate, toDate)

            val allTx = if (refresh) emptyList() else {
                (uiState.value as? DetailsUiState.Success)?.sections?.flatMap { it.items } ?: emptyList()
            } + (txResult.getOrNull()?.transactions ?: emptyList())

            val grouped = allTx
                .sortedByDescending { it.date }
                .groupBy { it.date.format(DateTimeFormatter.ofPattern("MMMM yyyy")) }
                .map { TransactionSection(it.key, it.value) }

            val hasMore = txResult.getOrNull()?.let { it.currentPage < it.totalPages } ?: false

            _uiState.value = DetailsUiState.Success(
                account = accountWithFavorite,
                sections = grouped,
                hasMore = hasMore,
                isFiltering = fromDate != null || toDate != null
            )

            if (hasMore) currentPage++

        }.invokeOnCompletion { throwable ->
            if (throwable != null && _uiState.value is DetailsUiState.Loading) {
                _uiState.value = DetailsUiState.Error("No internet connection", showRetry = true)
            }
        }

    }

    fun loadMore() {
        if ((_uiState.value as? DetailsUiState.Success)?.hasMore == true) {
            loadEverything(refresh = false)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            toggleFavoriteUseCase(accountId, getFavoriteOfflineUseCase().first()?.id)
        }
    }

    fun applyFilter(start: LocalDate?, end: LocalDate?) {
        fromDate = start?.atStartOfDay()?.atOffset(ZoneOffset.UTC)?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        toDate = end?.atTime(23, 59, 59)?.atOffset(ZoneOffset.UTC)?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        loadEverything(refresh = true)
    }

    fun clearFilter() {
        fromDate = null; toDate = null
        loadEverything(refresh = true)
    }

    fun retry() = loadEverything(refresh = true)

}