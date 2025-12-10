package com.raulespim.bankingaccounts.features.accounts.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.AccountDetailsDto
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.PagingDto
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.TransactionDto
import com.raulespim.bankingaccounts.features.accounts.data.remote.dto.TransactionsResponse
import com.raulespim.bankingaccounts.features.accounts.domain.model.Account
import com.raulespim.bankingaccounts.features.accounts.domain.model.Transaction
import com.raulespim.bankingaccounts.features.accounts.domain.repository.PagedTransactions
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetAccountDetailsUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetAccountsUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetFavoriteOfflineUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetTransactionsPageUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.ToggleFavoriteUseCase
import com.raulespim.bankingaccounts.provide.testAccountDetailsDtoMock
import com.raulespim.bankingaccounts.provide.testAccountMock
import com.raulespim.bankingaccounts.provide.testTransactionsPage1Mock
import com.raulespim.bankingaccounts.provide.testTransactionsPage2Mock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

class AccountDetailsViewModelTest {

    private val getAccountsUseCase: GetAccountsUseCase = mockk(relaxUnitFun = true)
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase = mockk(relaxed = true)
    private val getTransactionsPageUseCase: GetTransactionsPageUseCase = mockk(relaxed = true)
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk(relaxed = true)
    private val getFavoriteOfflineUseCase: GetFavoriteOfflineUseCase = mockk(relaxed = true)

    private lateinit var viewModel: AccountDetailsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    companion object {
        const val TEST_ACCOUNT_ID = "1f34c76a-b3d1-43bc-af91-a82716f1bc2e"
    }

    private val domainTransactionsPage1 =
        testTransactionsPage1Mock.transactions.map { Transaction.fromDto(it) }
    private val domainTransactionsPage2 =
        testTransactionsPage2Mock.transactions.map { Transaction.fromDto(it) }


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AccountDetailsViewModel {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["accountId"] = TEST_ACCOUNT_ID

        return AccountDetailsViewModel(
            savedStateHandle = savedStateHandle,
            getAccountsUseCase = getAccountsUseCase,
            getAccountDetailsUseCase = getAccountDetailsUseCase,
            getTransactionsPageUseCase = getTransactionsPageUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            getFavoriteOfflineUseCase = getFavoriteOfflineUseCase
        )
    }

    @Test
    fun `init - loads account details and first page of transactions successfully`() = runTest {
        coEvery { getAccountsUseCase() } returns flowOf(Result.success(listOf(testAccountMock)))
        coEvery { getAccountDetailsUseCase(TEST_ACCOUNT_ID) } returns Result.success(testAccountDetailsDtoMock)
        coEvery { getTransactionsPageUseCase(TEST_ACCOUNT_ID, 0, null, null) } returns Result.success(
            PagedTransactions(domainTransactionsPage1, currentPage = 0, totalPages = 3)
        )
        coEvery { getFavoriteOfflineUseCase() } returns flowOf(null)
        coEvery { getFavoriteOfflineUseCase() } returns flow {
            emit(null)
        }

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value as DetailsUiState.Success
        assertEquals("Current Account EUR", state.account.productName)
        assertEquals(2, state.sections.flatMap { it.items }.size)
        assertTrue(state.hasMore)
    }

    @Test
    fun `loadMore - requests next_page = 1 and appends transactions`() = runTest {
        coEvery { getAccountsUseCase() } returns flowOf(Result.success(listOf(testAccountMock)))
        coEvery { getAccountDetailsUseCase(any()) } returns Result.success(testAccountDetailsDtoMock)

        coEvery { getTransactionsPageUseCase(TEST_ACCOUNT_ID, 0, null, null) } returns Result.success(
            PagedTransactions(domainTransactionsPage1, currentPage = 0, totalPages = 3)
        )
        coEvery { getTransactionsPageUseCase(TEST_ACCOUNT_ID, 1, null, null) } returns Result.success(
            PagedTransactions(domainTransactionsPage2, currentPage = 1, totalPages = 3)
        )

        coEvery { getFavoriteOfflineUseCase() } returns flow { emit(null) }

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        val state = viewModel.uiState.value as DetailsUiState.Success
        assertEquals(2, state.sections.size)
        assertEquals("March 2025", state.sections[0].monthYear)
        assertEquals("February 2025", state.sections[1].monthYear)
        assertEquals(3, state.sections.flatMap { it.items }.size)
        assertTrue(state.hasMore)
    }

    @Test
    fun `loadMore - when current_page_plus_one_exceeds_pages_count then hasMore_becomes_false`() = runTest {
        val lastPage = PagedTransactions(
            transactions = domainTransactionsPage1,
            currentPage = 0,
            totalPages = 1
        )

        coEvery { getAccountsUseCase() } returns flowOf(Result.success(listOf(testAccountMock)))
        coEvery { getAccountDetailsUseCase(any()) } returns Result.success(testAccountDetailsDtoMock)
        coEvery { getTransactionsPageUseCase(TEST_ACCOUNT_ID, 0, null, null) } returns Result.success(lastPage)
        coEvery { getTransactionsPageUseCase(TEST_ACCOUNT_ID, 1, null, null) } returns Result.success(
            PagedTransactions(emptyList(), currentPage = 1, totalPages = 1)
        )
        coEvery { getFavoriteOfflineUseCase() } returns flow { emit(null) }

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        val state = viewModel.uiState.value as DetailsUiState.Success
        assertFalse(state.hasMore)
    }

    @Test
    fun `applyFilter - sends from_date and to_date in ISO format and resets page`() = runTest {
        coEvery { getAccountsUseCase() } returns flowOf(Result.success(listOf(testAccountMock)))
        coEvery { getAccountDetailsUseCase(any()) } returns Result.success(testAccountDetailsDtoMock)
        coEvery { getTransactionsPageUseCase(TEST_ACCOUNT_ID, 0, any(), any()) } returns Result.success(
            PagedTransactions(domainTransactionsPage1, currentPage = 0, totalPages = 1)
        )

        coEvery { getFavoriteOfflineUseCase() } returns flow { emit(null) }

        viewModel = createViewModel()
        advanceUntilIdle()

        val start = LocalDate.of(2025, 3, 1)
        val end = LocalDate.of(2025, 3, 31)
        viewModel.applyFilter(start, end)
        advanceUntilIdle()

        val state = viewModel.uiState.value as DetailsUiState.Success
        assertTrue(state.isFiltering)

        coVerify(exactly = 1) {
            getTransactionsPageUseCase(
                accountId = TEST_ACCOUNT_ID,
                page = 0,
                from = "2025-03-01T00:00:00Z",
                to = "2025-03-31T23:59:59Z"
            )
        }
    }

    @Test
    fun `toggleFavorite - calls use case with correct parameters`() = runTest {
        val currentFavorite = testAccountMock.copy(id = "other-id")

        coEvery { getAccountsUseCase() } returns flowOf(Result.success(listOf(testAccountMock)))
        coEvery { getAccountDetailsUseCase(any()) } returns Result.success(testAccountDetailsDtoMock)
        coEvery { getTransactionsPageUseCase(any(), any(), any(), any()) } returns Result.success(
            PagedTransactions(emptyList(), currentPage = 0, totalPages = 1)
        )
        coEvery { getFavoriteOfflineUseCase() } returns flow { emit(currentFavorite) }

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite()

        coVerify(exactly = 1) { toggleFavoriteUseCase(TEST_ACCOUNT_ID, "other-id") }
    }

    @Test
    fun `observeFavoriteStatus - updates isFavorite when favorite changes externally`() = runTest {
        coEvery { getAccountsUseCase() } returns flowOf(Result.success(listOf(testAccountMock)))
        coEvery { getAccountDetailsUseCase(any()) } returns Result.success(testAccountDetailsDtoMock)
        coEvery { getTransactionsPageUseCase(any(), any(), any(), any()) } returns Result.success(
            PagedTransactions(emptyList(), currentPage = 0, totalPages = 1)
        )

        val favoriteFlow = MutableSharedFlow<Account?>(replay = 1)
        coEvery { getFavoriteOfflineUseCase() } returns favoriteFlow

        viewModel = createViewModel()
        advanceUntilIdle()

        favoriteFlow.tryEmit(testAccountMock.copy(isFavorite = true))
        advanceUntilIdle()

        val state = viewModel.uiState.value as DetailsUiState.Success
        assertTrue(state.account.isFavorite)
    }

}