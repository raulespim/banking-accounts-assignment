package com.raulespim.bankingaccounts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.raulespim.bankingaccounts.core.navigation.Route
import com.raulespim.bankingaccounts.features.accounts.presentation.details.AccountDetailsScreen
import com.raulespim.bankingaccounts.features.accounts.presentation.list.AccountsScreen
import com.raulespim.bankingaccounts.ui.theme.BankingAccountsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BankingAccountsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Route.AccountsScreen,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    ) {
                        composable<Route.AccountsScreen> {
                            AccountsScreen(
                                onAccountClick = { accountId ->
                                    navController.navigate(Route.AccountDetailsScreen(accountId))
                                }
                            )
                        }
                        composable<Route.AccountDetailsScreen> {
                            val accountId = it.toRoute<Route.AccountDetailsScreen>().accountId
                            AccountDetailsScreen(
                                accountId = accountId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

