package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random

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
                state.position = Offset(window.x + state.width / 2f, window.y + state.height / 2f)
                if (window.isMinimized) gameState.resourceFields -= state

                if (state.isRevealed && !state.isCollapsed && !window.isActive) state.stability -= 0.001f

                if (state.stability < 0.95) {
                    val chance = 1 - state.stability
                    if (Random.nextFloat() < chance * chance * chance) {
                        state.isCollapsed = true
                    }
                }
            }
        }
        Column {
            if (state.isCollapsed) {
                Text("Collapsed ${state.stability}")
            } else if (state.isRevealed) {
                if (state.inventory.isEmpty()) Text("no resources found")
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

                Text("Stability: ${floor(state.stability * 1000) / 10}%")
            } else {
                Text("Reveal progress: ${floor(state.revealProgress * 100)}%")
                Button(onClick = { state.revealProgress += gameState.player.resourceRevealSpeed }) {
                    Text("Dig")
                }
            }
        }
    }
}
