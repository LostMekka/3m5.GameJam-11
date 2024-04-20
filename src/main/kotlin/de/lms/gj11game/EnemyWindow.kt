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
fun EnemyWindow(onDeath: () -> Unit, player: PlayerState, initialPosition: IntOffset = IntOffset.Zero) {
    val maxHealth = 20
    val health = remember { mutableStateOf(maxHealth) }
    val state = rememberWindowState(
        position = WindowPosition(initialPosition.x.dp, initialPosition.y.dp),
        width = 200.dp,
        height = 100.dp,
    )
    val movement = remember { mutableStateOf(IntOffset.Zero) }

    val onClick = {
        health.value -= player.baseDamage
        if (health.value <= 0) {
            onDeath()
        }
    }

    Window(
        onCloseRequest = onClick,
        state = state,
        resizable = false,
        title = "Enemy ${health.value} / $maxHealth"
    ) {
        LaunchedEffect(state) {
            while (true) {
                delay(100)
                if (state.isMinimized) {
                    onClick()
                    state.isMinimized = false
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

