package com.abitusd.app.feature.clock.data

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.abitusd.app.R
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockRun
import com.abitusd.app.feature.clock.domain.ClockSpec
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** Quick settings tile (11e): starts the stopwatch, or finishes the running clock. */
@AndroidEntryPoint
class ClockTileService : TileService() {
    @Inject lateinit var controller: ClockController

    override fun onStartListening() = refresh()

    override fun onClick() {
        if (controller.store.refresh() != null) {
            controller.stop()
        } else {
            val name = getString(R.string.clock_preset_stopwatch)
            controller.start(
                ClockRun(name, ClockMode.STOPWATCH.key, ClockSpec(emptyList()), System.currentTimeMillis()),
            )
        }
        refresh()
    }

    private fun refresh() {
        val tile = qsTile ?: return
        tile.state = if (controller.store.refresh() != null) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }
}
