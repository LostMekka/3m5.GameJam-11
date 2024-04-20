package de.lms.gj11game

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay


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
        Button(
            onClick = { state.player.statsWindowVisible = true },
            enabled = !state.player.statsWindowVisible,
        ) {
            Text("Show player stats")
        }
    }
}
