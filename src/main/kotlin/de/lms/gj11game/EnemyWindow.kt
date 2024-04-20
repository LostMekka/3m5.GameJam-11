package de.lms.gj11game

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay

@Composable
fun EnemyWindows(state: GameState) {
    for (enemy in state.enemies) EnemyWindow(enemy, state.player) { state.enemies -= enemy }
}

@Composable
fun EnemyWindow(state: EnemyState, player: PlayerState, onDeath: () -> Unit) {
    val movement = remember { mutableStateOf(IntOffset.Zero) }

    val onClick = {
        state.hp -= player.baseDamage
        if (state.hp <= 0) onDeath()
    }

    Window(
        onCloseRequest = onClick,
        state = WindowState(
            position = WindowPosition((state.position.x - state.width / 2).dp, (state.position.y - state.height / 2).dp),
            width = state.width.dp,
            height = state.height.dp,
        ),
        resizable = false,
        title = "Enemy ${state.hp} / ${state.maxHp}"
    ) {
        LaunchedEffect(state) {
            while (true) {
                delay(100)
                state.position = IntOffset(window.x + state.width / 2, window.y + state.height / 2)
                if (window.isMinimized) {
                    onClick()
                    window.isMinimized = false
                }
            }
        }

        Row {
            Button(onClick = onClick) {
                Text("Attack")
            }
            Text(state.id.toString())
        }
    }
}

