package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import de.lms.gj11game.helper.randomlyPositionedWindowState
import kotlinx.coroutines.delay


@Composable
fun ResourceInventoryWindow(state: GameState) {
    Window(
        onCloseRequest = {},
        state = remember { randomlyPositionedWindowState(300f, 500f) },
        resizable = false,
        title = "Collected Resources",
    ) {
        LaunchedEffect(key1 = state) {
            while (true) {
                delay(100)
                if (window.isMinimized) window.isMinimized = false
            }
        }
        Column {
            if (state.inventory.isClear()) Text("Nothing here...")
            for (type in ResourceType.entries) {
                if (type in state.inventory) {
                    Text("$type: ${state.inventory[type]}")
                }
            }
        }
    }
}
