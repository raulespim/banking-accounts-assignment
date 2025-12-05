package com.raulespim.bankingaccounts.features.accounts.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raulespim.bankingaccounts.features.accounts.presentation.list.components.AccountCard
import com.raulespim.bankingaccounts.ui.theme.BankingAccountsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onAccountClick: (String) -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Accounts", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->

        when (uiState) {
            is AccountsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is AccountsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                }
            }

            is AccountsUiState.Success -> {
                if (uiState.accounts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No accounts found")
                    }
                } else {
                    PullToRefreshBox(
                        isRefreshing = uiState is AccountsUiState.Loading,
                        onRefresh = { viewModel.refreshAccounts() },
                        modifier = Modifier.padding(padding)
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.accounts, key = { it.id }) { account ->
                                AccountCard(
                                    account = account,
                                    onClick = { onAccountClick(account.id) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AccountsScreenPreview() {
    BankingAccountsTheme {
        AccountsScreen(
            {}
        )
    }
}