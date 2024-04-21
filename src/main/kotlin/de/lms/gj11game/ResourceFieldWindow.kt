package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import de.lms.gj11game.helper.playerInInteractionRange
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.math.max
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
            width = state.position.width.dp,
            height = state.position.height.dp,
            position = WindowPosition(state.position.x.dp, state.position.y.dp),
        ),
        resizable = false,
        title = "Resource Field",
    ) {
        LaunchedEffect(key1 = state) {
            while (true) {
                delay(100)
                state.position = state.position.withPosition(window.x.toFloat(), window.y.toFloat())
                if (window.isMinimized) gameState.resourceFields -= state

                if (state.isRevealed && !state.isCollapsed && !window.isActive)
                    state.stability = max(0f, state.stability - 0.025f)

                if (state.stability <= 0 && Random.nextFloat() < 0.05)
                    state.isCollapsed = true
            }
        }
        Column {
            if (state.isCollapsed) {
                Text("Collapsed!")
            } else {
                val inRange = gameState.playerInInteractionRange(state.position)
                if (!inRange) Text("out of range!")

                if (state.isRevealed) {
                    if (state.inventory.isClear()) Text("no resources found")

                    for ((type, amount) in state.inventory) {
                        Button(
                            onClick = {
                                val n = min(gameState.player.resourceMiningSpeed, amount)
                                gameState.inventory[type] += n
                                state.inventory[type] -= n
                            },
                            enabled = inRange && amount > 0,
                        ) {
                            Text("Mine $type ($amount)")
                        }
                    }

                    Text("Stability: ${floor(state.stability * 1000) / 10}%")
                } else {
                    Text("Reveal progress: ${floor(state.revealProgress * 100)}%")
                    Button(
                        onClick = { state.revealProgress += gameState.player.resourceRevealSpeed },
                        enabled = inRange,
                    ) {
                        Text("Dig")
                    }
                }
            }
        }
    }
}
