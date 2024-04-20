package de.lms.gj11game

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlin.random.Random

class GameState {
    var score by mutableStateOf(0)
    var player by mutableStateOf(PlayerState())
}

class PlayerState {
    var hp by mutableStateOf(100)
    var baseDamage by mutableStateOf(1)
    var statsWindowVisible by mutableStateOf(false)
    var position by mutableStateOf(IntOffset(0, 0))
}

@Composable
fun MovingWindow(onCloseRequest: () -> Unit) {
    val position = remember { mutableStateOf(IntOffset(200, 200)) }
    val size = remember { mutableStateOf(IntSize(300, 200)) }

    LaunchedEffect(key1 = position) {
        while (true) {
            delay(100)
            position.value = IntOffset(
                x = Random.nextInt(0, 800),
                y = Random.nextInt(0, 600),
            )
        }
    }

    Window(
        onCloseRequest = onCloseRequest,
        state = WindowState(
            position = WindowPosition(position.value.x.dp, position.value.y.dp),
            width = size.value.width.dp,
            height = size.value.height.dp,
        ),
        title = "Moving Window"
    ) {
        Text("hello!")
    }
}

@Composable
@Preview
fun App() {
    val state = remember { GameState() }

    MaterialTheme {
        Column {
            Text("score: ${state.score}")
            Text("hp: ${state.player.hp}")
            Button(onClick = { state.score++ }) {
                Text("score++")
            }
            Button(onClick = { state.player.hp-- }) {
                Text("hp--")
            }
        }
    }
    MovingWindow {}
}

fun main() = application {
    val state = remember { GameState() }
    MainWindow(state, this)
    LaunchedEffect(key1 = this) { mainLoop(state) }
}
