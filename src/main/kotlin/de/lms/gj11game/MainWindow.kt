package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import kotlin.math.floor


@Composable
fun MainWindow(state: GameState, applicationScope: ApplicationScope) {
    Window(
        onCloseRequest = applicationScope::exitApplication,
        state = WindowState(
            width = 500.dp,
            height = 300.dp,
        ),
        title = "This is a Game! Don't let anyone tell you otherwise!",
    ) {
        PlayerWindow(state.player)
        PlayerInfoWindow(state.player)
        EnemyWindows(state)
        ResourceFieldWindows(state)
        ResourceInventoryWindow(state)
        state.moving?.let {
            AreaMovementWindows(it, state)
        }

        Column {
            Button(
                onClick = { state.player.statsWindowVisible = true },
                enabled = !state.player.statsWindowVisible,
            ) {
                Text("Show player stats")
            }

            val progress = state.resourceScanningProgress
            Button(
                onClick = { state.resourceScanningProgress = 0f },
                enabled = progress == null,
            ) {
                if (progress == null) {
                    Text("Scan for resource deposit")
                } else {
                    Text("Scanning for resource deposit... (${floor(progress * 100)}%)")
                }
            }

            CraftingStationUnlockView(state.craftingStation, state)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { state.moving = MovingState() },
                    enabled = state.moving == null,
                ) {
                    Text("Move to different area")
                }
                Spacer(Modifier.width(10.dp))
                Text("You are currently in ${state.currentArea}")
            }

        }
    }
}
