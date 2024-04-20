package de.lms.gj11game

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntOffset
import java.util.*

enum class ResourceType {
    Stone,
    Wood,
    Iron,
    Steel,
    Gold,
    Diamonds,
    Mana,
    Meat,
    Bones,
}
data class ResourcePack(val type: ResourceType, val amount: Int)
operator fun ResourceType.times(amount: Int) = ResourcePack(this, amount)

class GameState {
    var score by mutableStateOf(0)
    var resourceScanningProgress by mutableStateOf<Float?>(null)
    val inventory = Inventory()
    var player by mutableStateOf(PlayerState())
    val enemies = mutableStateListOf<EnemyState>()
    val resourceFields = mutableStateListOf<ResourceFieldState>()
}

class Inventory(vararg resources: ResourcePack) {
    private val stateMap = mutableStateMapOf<ResourceType, Int>().also {
        for ((type, amount) in resources) it[type] = amount
    }
    operator fun get(type: ResourceType) = stateMap[type] ?: 0
    operator fun set(type: ResourceType, value: Int) { stateMap[type] = value }
    operator fun iterator() = stateMap.iterator()
    operator fun contains(type: ResourceType) = stateMap.containsKey(type)
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
    var resourceScanningSpeed by mutableStateOf(0.4f) // TODO: make smaller initially
    var resourceRevealSpeed by mutableStateOf(0.1f) // TODO: make smaller initially
    var resourceMiningSpeed by mutableStateOf(1)
    var statsWindowVisible by mutableStateOf(false)
    var width by mutableStateOf(400)
    var height by mutableStateOf(200)
    var position by mutableStateOf(IntOffset(width / 2, height / 2))
}

class ResourceFieldState(x: Int, y: Int, vararg resources: ResourcePack) {
    val id: UUID = UUID.randomUUID()
    var revealProgress by mutableStateOf(0f)
    val isRevealed get() = revealProgress >= 1f
    val inventory = Inventory(*resources)
    var width by mutableStateOf(300)
    var height by mutableStateOf(300)
    var position by mutableStateOf(IntOffset(x + width / 2, y + height / 2))

    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = other is ResourceFieldState && other.id == id
}
