package com.raulespim.bankingaccounts.features.accounts.presentation.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onDatesSelected: (LocalDate?, LocalDate?) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DateRangePicker(
        state = dateRangePickerState,
        modifier = Modifier.fillMaxSize(),
        title = { Text("Select date range", modifier = Modifier.padding(16.dp)) },
        headline = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateRangePickerState.selectedStartDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    } ?: "Start",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(" → ")
                Text(
                    text = dateRangePickerState.selectedEndDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    } ?: "End",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        showModeToggle = true
    )

    // Bottom buttons
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
        Spacer(Modifier.width(8.dp))
        TextButton(
            onClick = {
                val start = dateRangePickerState.selectedStartDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                }
                val end = dateRangePickerState.selectedEndDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                }
                onDatesSelected(start, end)
                onDismiss()
            },
            enabled = dateRangePickerState.selectedEndDateMillis != null
        ) {
            Text("OK")
        }
    }
}