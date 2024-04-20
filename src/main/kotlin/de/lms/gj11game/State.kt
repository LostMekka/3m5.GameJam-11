package de.lms.gj11game

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntOffset
import java.util.*

class GameState {
    var score by mutableStateOf(0)
    var resourceScanningProgress by mutableStateOf<Float?>(null)
    var player by mutableStateOf(PlayerState())
    val enemies = mutableStateListOf<EnemyState>()
    val resourceFields = mutableStateListOf<ResourceFieldState>()
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
    var resourceScanningSpeed by mutableStateOf(0.02f)
    var resourceRevealSpeed by mutableStateOf(0.05f)
    var resourceMiningSpeed by mutableStateOf(1)
    var statsWindowVisible by mutableStateOf(false)
    var width by mutableStateOf(400)
    var height by mutableStateOf(200)
    var position by mutableStateOf(IntOffset(width / 2, height / 2))
}

class ResourceFieldState(x: Int, y: Int, amount: Int) {
    val id: UUID = UUID.randomUUID()
    var revealProgress by mutableStateOf(0f)
    val isRevealed get() = revealProgress >= 1f
    var amount by mutableStateOf(amount)
    var width by mutableStateOf(300)
    var height by mutableStateOf(300)
    var position by mutableStateOf(IntOffset(x + width / 2, y + height / 2))

    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = other is ResourceFieldState && other.id == id
}
