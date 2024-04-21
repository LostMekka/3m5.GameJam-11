package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay


@Composable
fun PlayerWindow(state: PlayerState) {
    Window(
        onCloseRequest = { state.statsWindowVisible = false },
        state = WindowState(
            width = state.position.width.dp,
            height = state.position.height.dp,
            position = WindowPosition(state.position.x.dp, state.position.y.dp),
        ),
        resizable = false,
        title = "Player",
        alwaysOnTop = true,
    ) {
        LaunchedEffect(key1 = state) {
            while (true) {
                delay(100)
                state.position = state.position.withPosition(window.x.toFloat(), window.y.toFloat())
                if (window.isMinimized) {
                    window.toolkit.beep()
                    window.isMinimized = false
                }
            }
        }
        Column {
            Text("HP: ${state.hp}")
        }
    }
}
