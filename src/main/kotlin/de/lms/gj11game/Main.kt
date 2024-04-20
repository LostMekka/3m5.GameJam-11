package de.lms.gj11game

import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.application

class GameState {
    var score by mutableStateOf(0)
    var player by mutableStateOf(PlayerState())
    val enemies = mutableStateListOf<EnemyState>()
}

class EnemyState(hp: Int, x: Int, y: Int) {
    var hp by mutableStateOf(hp)
    val maxHp = hp
    var position by mutableStateOf(IntOffset(x, y))
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
