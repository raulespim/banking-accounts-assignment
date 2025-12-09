package com.raulespim.bankingaccounts.features.accounts.presentation.details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raulespim.bankingaccounts.features.accounts.domain.model.Account
import com.raulespim.bankingaccounts.ui.theme.BankingAccountsTheme
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

@Composable
fun AccountHeader(account: Account) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(account.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(account.accountType.replace("_", " ").uppercase(), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Text(
                text = formatBalance(account.balance, account.currencyCode),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            account.productName?.let {
                Spacer(Modifier.height(8.dp))
                Text("Product: $it", style = MaterialTheme.typography.bodyMedium)
            }

            account.openedDate?.let { isoDate ->
                val formatted = try {
                    LocalDateTime.parse(isoDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                } catch (e: Exception) { isoDate }
                Text("Opened: $formatted", style = MaterialTheme.typography.bodyMedium)
            }

            account.branch?.let {
                Text("Branch: $it", style = MaterialTheme.typography.bodyMedium)
            }

            if (account.beneficiaries.isNotEmpty()) {
                Text("Beneficiaries: ${account.beneficiaries.joinToString()}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountHeaderPreview() {
    BankingAccountsTheme {
        AccountHeader(
            Account(
                id = "1f34c76a-b3d1-43bc-af91-a82716f1bc2e",
                accountNumber = 12345,
                balance = "99.00",
                currencyCode = "EUR",
                accountType = "current",
                accountNickname = "My Salary"
            )
        )
    }
}

private fun formatBalance(amount: String, currencyCode: String): String {
    return try {
        val num = amount.toDouble()
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
        formatter.currency = Currency.getInstance(currencyCode)
        formatter.format(num)
    } catch (e: Exception) {
        "$amount $currencyCode"
    }
}