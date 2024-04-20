package de.lms.gj11game

import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.application

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

fun main() = application {
    val state = remember { GameState() }
    MainWindow(state, this)
    LaunchedEffect(key1 = this) { mainLoop(state) }
}
