package com.raulespim.bankingaccounts.features.accounts.presentation

import com.raulespim.bankingaccounts.features.accounts.domain.usecase.GetAccountsUseCase
import com.raulespim.bankingaccounts.features.accounts.domain.usecase.RefreshAccountsUseCase
import com.raulespim.bankingaccounts.features.accounts.presentation.list.AccountsUiState
import com.raulespim.bankingaccounts.features.accounts.presentation.list.AccountsViewModel
import com.raulespim.bankingaccounts.provide.emptyAccountList
import com.raulespim.bankingaccounts.provide.favoriteAccountMock
import com.raulespim.bankingaccounts.provide.regularAccountMock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.IOException


@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {

    private val getAccountsUseCase: GetAccountsUseCase = mockk(relaxUnitFun = true)
    private val refreshAccountsUseCase: RefreshAccountsUseCase = mockk(relaxed = true)
    private lateinit var viewModel: AccountsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init - loads accounts and puts favorites first`() = runTest {
        val favorite = favoriteAccountMock
        val normal = regularAccountMock

        every { getAccountsUseCase() } returns flowOf(Result.success(listOf(normal, favorite)))

        viewModel = AccountsViewModel(getAccountsUseCase, refreshAccountsUseCase)

        advanceUntilIdle()

        val state = viewModel.uiState.value as AccountsUiState.Success

        assertEquals("2", state.accounts[0].id) // favorite first
        assertEquals("1", state.accounts[1].id)

        coVerify(exactly = 1) { refreshAccountsUseCase() }
    }

    @Test
    fun `init - emits empty Success when no accounts`() = runTest {
        coEvery { getAccountsUseCase() } returns flowOf(Result.success(emptyAccountList))

        viewModel = AccountsViewModel(getAccountsUseCase, refreshAccountsUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AccountsUiState.Success
        assertTrue(state.accounts.isEmpty())

        coVerify(exactly = 1) { refreshAccountsUseCase() }
    }

    @Test
    fun `init - emits Error when getAccountsUseCase fails`() = runTest {
        val exception = RuntimeException("No internet")
        coEvery { getAccountsUseCase() } returns flowOf(Result.failure(exception))

        viewModel = AccountsViewModel(getAccountsUseCase, refreshAccountsUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AccountsUiState.Error
        assertEquals("No internet connection", state.message)
    }

    @Test
    fun `init - emits Unexpected error when flow throws`() = runTest {
        coEvery { getAccountsUseCase() } returns flow {
            throw IllegalStateException("Database exploded")
        }

        viewModel = AccountsViewModel(getAccountsUseCase, refreshAccountsUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AccountsUiState.Error
        assertEquals("Unexpected error", state.message)
    }

}