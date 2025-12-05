package com.raulespim.bankingaccounts.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object AccountsScreen: Route

    @Serializable
    data class AccountDetailsScreen(val accountId: String): Route
}