package de.lms.gj11game

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
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay

@Composable
fun EnemyWindows(state: GameState) {
    for (enemy in state.enemies) EnemyWindow(enemy, state.player) { state.enemies -= enemy }
}

@Composable
fun EnemyWindow(state: EnemyState, player: PlayerState, onDeath: () -> Unit) {
    val windowState = rememberWindowState(
        position = WindowPosition(state.position.x.dp, state.position.y.dp),
        width = 400.dp,
        height = 100.dp,
    )
    val movement = remember { mutableStateOf(IntOffset.Zero) }

    val onClick = {
        state.hp -= player.baseDamage
        if (state.hp <= 0) onDeath()
    }

    Window(
        onCloseRequest = onClick,
        state = windowState,
        resizable = false,
        title = "Enemy ${state.hp} / ${state.maxHp}"
    ) {
        LaunchedEffect(windowState) {
            while (true) {
                delay(100)
                if (windowState.isMinimized) {
                    onClick()
                    windowState.isMinimized = false
                }
            }
        }

        Button(
            onClick = onClick
        ) {
            Text("Attack")
        }
    }
}

