package com.raulespim.bankingaccounts.features.accounts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.raulespim.bankingaccounts.core.navigation.Route
import com.raulespim.bankingaccounts.ui.theme.BankingAccountsTheme

@Composable
fun AccountsScreen(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onClick
            ) {
                Text("Go to Account Details Screen")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AccountsScreenPreview() {
    BankingAccountsTheme {
        AccountsScreen(
            {}
        )
    }
}