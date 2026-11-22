package com.abitusd.app.feature.library.ui.list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.domain.EquipmentProfile

/** Chooses the place (gym, home, park) whose equipment filters the library. */
@Composable
fun EquipmentProfileRow(
    selected: EquipmentProfile,
    onSelect: (EquipmentProfile) -> Unit,
) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = AppSpacing.screenMargin),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(R.string.profile_equipment),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
        )
        EquipmentProfile.entries.forEach { profile ->
            FilterChip(selected = profile == selected, onClick = {
                onSelect(profile)
            }, label = { Text(stringResource(profile.label)) })
        }
    }
}
