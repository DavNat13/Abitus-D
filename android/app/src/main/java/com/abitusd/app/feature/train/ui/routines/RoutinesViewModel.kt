package com.abitusd.app.feature.train.ui.routines

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.PlanningDao
import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.feature.train.data.TemplateInstaller
import com.abitusd.app.feature.train.domain.PlanTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MS = 5000L

/** Routines grouped by folder, archived ones last, and template installation. */
@HiltViewModel
class RoutinesViewModel
    @Inject
    constructor(
        dao: PlanningDao,
        private val installer: TemplateInstaller,
    ) : ViewModel() {
        val routines: StateFlow<List<RoutineEntity>> =
            dao.routines().stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

        private val mutableMessage = MutableStateFlow<Int?>(null)

        @get:StringRes
        val message: StateFlow<Int?> = mutableMessage.asStateFlow()

        fun install(template: PlanTemplate) {
            viewModelScope.launch {
                val ok = installer.install(template)
                mutableMessage.value = if (ok) R.string.train_template_done else R.string.train_template_needs_catalog
            }
        }
    }
