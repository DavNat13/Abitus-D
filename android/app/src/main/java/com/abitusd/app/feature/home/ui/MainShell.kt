package com.abitusd.app.feature.home.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme

enum class MainTab(
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
    @param:StringRes val emptyText: Int,
) {
    HOME(R.string.nav_home, AppIcons.Home, R.string.home_empty),
    TRAIN(R.string.nav_train, AppIcons.Train, R.string.train_empty),
    PROGRESS(R.string.nav_progress, AppIcons.Progress, R.string.progress_empty),
    LIBRARY(R.string.nav_library, AppIcons.Library, R.string.library_empty),
}

/** Bottom bar with four sections (05-UI-UX.md). Profile opens from the Home top bar. */
@Composable
fun MainShell(onOpenProfile: () -> Unit) {
    val colors = AppTheme.colors
    var selected by rememberSaveable { mutableStateOf(MainTab.HOME) }
    Scaffold(
        containerColor = colors.background,
        bottomBar = {
            NavigationBar(containerColor = colors.surface) {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selected,
                        onClick = { selected = tab },
                        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                        label = { Text(stringResource(tab.label), style = AppTheme.typography.caption) },
                        colors =
                            NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.primary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primaryContainer,
                                unselectedIconColor = colors.textSecondary,
                                unselectedTextColor = colors.textSecondary,
                            ),
                    )
                }
            }
        },
    ) { padding ->
        SectionPlaceholder(
            tab = selected,
            onOpenProfile = if (selected == MainTab.HOME) onOpenProfile else null,
            modifier = Modifier.padding(padding),
        )
    }
}
