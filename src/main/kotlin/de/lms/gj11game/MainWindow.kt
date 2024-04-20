package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
        CharacterInfoWindow(state.player)
        EnemyWindows(state)
        ResourceFieldWindows(state)
        Column {
            Text("Score: ${state.score}")

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
        }
    }
}
