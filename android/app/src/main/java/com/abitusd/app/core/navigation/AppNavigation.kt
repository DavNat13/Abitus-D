package com.abitusd.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.abitusd.app.feature.home.ui.HomeScreen

/** Navigation 3 host. Phase 0 has a single placeholder destination. */
@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<Any>(HomeRoute) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<HomeRoute> { HomeScreen() }
            },
    )
}
