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
fun CharacterInfoWindow(state: PlayerState) {
    if (!state.statsWindowVisible) return
    Window(
        onCloseRequest = { state.statsWindowVisible = false },
        state = WindowState(
            width = state.width.dp,
            height = state.height.dp,
            position = WindowPosition((state.position.x - state.width / 2).dp, (state.position.y - state.height / 2).dp),
        ),
        resizable = false,
        title = "Character Information",
    ) {
        LaunchedEffect(key1 = state) {
            while (true) {
                delay(100)
                state.position = IntOffset(window.x + state.width / 2, window.y + state.height / 2)
                if (window.isMinimized) {
                    window.toolkit.beep()
                    window.isMinimized = false
                }
            }
        }
        Column {
            Text("HP: ${state.hp}")
            Text("Base damage: ${state.baseDamage}")
        }
    }
}
