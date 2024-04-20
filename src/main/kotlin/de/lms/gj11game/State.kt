package de.lms.gj11game

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntOffset
import java.util.*

class GameState {
    var score by mutableStateOf(0)
    var player by mutableStateOf(PlayerState())
    val enemies = mutableStateListOf<EnemyState>()
}

class EnemyState(hp: Int, x: Int, y: Int, width: Int = 400, height: Int = 100) {
    val id: UUID = UUID.randomUUID()
    var hp by mutableStateOf(hp)
    val maxHp = hp
    var width by mutableStateOf(width)
    var height by mutableStateOf(height)
    var position by mutableStateOf(IntOffset(x, y))

    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = other is EnemyState && other.id == id
}

class PlayerState {
    var hp by mutableStateOf(100)
    var baseDamage by mutableStateOf(1)
    var statsWindowVisible by mutableStateOf(false)
    var width by mutableStateOf(400)
    var height by mutableStateOf(200)
    var position by mutableStateOf(IntOffset(width / 2, height / 2))
}
