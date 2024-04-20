package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay


@Composable
fun PlayerInfoWindow(state: PlayerState) {
    if (!state.statsWindowVisible) return
    Window(
        onCloseRequest = { state.statsWindowVisible = false },
        state = WindowState(
            width = state.infoWidth.dp,
            height = state.infoHeight.dp,
            position = WindowPosition((state.infoPosition.x - state.infoWidth / 2).dp, (state.infoPosition.y - state.infoHeight / 2).dp),
        ),
        resizable = false,
        title = "Character Information",
    ) {
        LaunchedEffect(key1 = state) {
            while (true) {
                delay(100)
                state.infoPosition = IntOffset(window.x + state.infoWidth / 2, window.y + state.infoHeight / 2)
                if (window.isMinimized) {
                    state.statsWindowVisible = false
                }
            }
        }
        Column {
            Text("HP: ${state.hp}")
            Text("Base damage: ${state.baseDamage}")
        }
    }
}
