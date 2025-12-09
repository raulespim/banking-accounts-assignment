package com.raulespim.bankingaccounts.features.accounts.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.raulespim.bankingaccounts.R
import com.raulespim.bankingaccounts.features.accounts.presentation.details.components.AccountHeader
import com.raulespim.bankingaccounts.features.accounts.presentation.details.components.DateRangePickerDialog
import com.raulespim.bankingaccounts.features.accounts.presentation.details.components.TransactionRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    accountId: String,
    onNavigateBack: () -> Unit,
    viewmodel: AccountDetailsViewModel = hiltViewModel()
) {
    val state by viewmodel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Account Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    when (state) {
                        is DetailsUiState.Success -> {
                            val stateSuccess = state as DetailsUiState.Success
                            IconButton(onClick = viewmodel::toggleFavorite) {
                                if (stateSuccess.account.isFavorite) {
                                    Icon(
                                        Icons.Filled.Star,
                                        "Favorite",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(R.raw.unfavorite)
                                            .decoderFactory(SvgDecoder.Factory())
                                            .build(),
                                        contentDescription = "Unfavofite",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                            }
                            IconButton(onClick = { showFilterDialog = true }) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(R.raw.filter)
                                        .decoderFactory(SvgDecoder.Factory())
                                        .build(),
                                    contentDescription = "Filter",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            if (stateSuccess.isFiltering) TextButton(onClick = viewmodel::clearFilter) { Text("Clear") }
                        }
                        else -> Unit
                    }
                }
            )
        }
    ) { padding ->

        when (val uiState = state) {
            is DetailsUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            is DetailsUiState.Error -> {
                Column(Modifier.fillMaxSize().padding(32.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                    Text(uiState.msg, color = MaterialTheme.colorScheme.error)
                    if (uiState.showRetry) {
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = viewmodel::retry) { Text("Retry") }
                    }
                }
            }
            is DetailsUiState.Success -> {
                LazyColumn(state = listState, contentPadding = PaddingValues(top = padding.calculateTopPadding())) {
                    item { AccountHeader(uiState.account) }

                    uiState.sections.forEach { section ->
                        stickyHeader {
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
                                Text(
                                    section.monthYear,
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        items(section.items) { tx ->
                            TransactionRow(tx)
                        }
                    }

                    if (uiState.hasMore) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                                CircularProgressIndicator()
                            }
                            LaunchedEffect(Unit) { viewmodel.loadMore() }
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter transactions by date") },
            text = {
                DateRangePickerDialog(
                    onDismiss = { showFilterDialog = false },
                    onDatesSelected = { start, end ->
                        viewmodel.applyFilter(start, end)
                        showFilterDialog = false
                    }
                )
            },
            confirmButton = {},   // buttons are inside the picker
            dismissButton = {}
        )
    }
}

