package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.math.min

@Composable
fun ResourceFieldWindows(state: GameState) {
    for (field in state.resourceFields) ResourceFieldWindow(field, state)
}

@Composable
fun ResourceFieldWindow(state: ResourceFieldState, gameState: GameState) {
    Window(
        onCloseRequest = { gameState.resourceFields -= state },
        state = WindowState(
            width = state.width.dp,
            height = state.height.dp,
            position = WindowPosition(
                (state.position.x - state.width / 2).dp,
                (state.position.y - state.height / 2).dp,
            ),
        ),
        resizable = false,
        title = "Resource Field",
    ) {
        LaunchedEffect(key1 = state) {
            while (true) {
                delay(100)
                state.position = IntOffset(window.x + state.width / 2, window.y + state.height / 2)
                if (window.isMinimized) gameState.resourceFields -= state
            }
        }
        Column {
            if (state.isRevealed) {
                for ((type, amount) in state.inventory) {
                    Button(
                        onClick = {
                            val n = min(gameState.player.resourceMiningSpeed, amount)
                            gameState.inventory[type] += n
                            state.inventory[type] -= n
                        },
                        enabled = amount > 0,
                    ) {
                        Text("Mine $type ($amount)")
                    }
                }
            } else {
                Text("Reveal progress: ${floor(state.revealProgress * 100)}%")
                Button(onClick = { state.revealProgress += gameState.player.resourceRevealSpeed }) {
                    Text("Dig")
                }
            }
        }
    }
}
