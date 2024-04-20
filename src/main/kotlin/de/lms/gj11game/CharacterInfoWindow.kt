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
            width = 200.dp,
            height = 200.dp,
            position = WindowPosition(state.position.x.dp, state.position.y.dp),
        ),
        resizable = false,
        title = "Character Information",
    ) {
        LaunchedEffect(key1 = state) {
            while (true) {
                delay(100)
                state.position = IntOffset(window.x, window.y)
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
