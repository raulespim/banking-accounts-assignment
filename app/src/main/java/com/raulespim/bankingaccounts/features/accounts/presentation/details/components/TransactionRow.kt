package com.raulespim.bankingaccounts.features.accounts.presentation.details.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.raulespim.bankingaccounts.features.accounts.domain.model.Transaction
import com.raulespim.bankingaccounts.ui.theme.BankingAccountsTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TransactionRow(tx: Transaction) {
    ListItem(
        headlineContent = { tx.description?.let { Text(it) } ?: Text("Transaction") },
        supportingContent = { Text(tx.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy – HH:mm"))) },
        trailingContent = {
            Text(
                text = tx.signedAmount + " ${tx.currencyCode}",
                color = if (tx.isDebit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TransactionRowPreview() {
    BankingAccountsTheme {
        TransactionRow(
            Transaction(
                id = "a1a9e85b-0f21-451b-813f-44ebabff46c9",
                date = LocalDateTime.now(),
                amount = "199.21",
                type = "intrabank",
                description = null,
                isDebit = false
            )
        )
    }
}