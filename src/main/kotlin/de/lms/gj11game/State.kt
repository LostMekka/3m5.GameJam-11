package de.lms.gj11game

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import java.util.*

enum class AreaType {
    Plains,
    Forest,
    Hills,
    Mountains,
    Caves,
    Dungeon,
}

enum class ResourceType {
    Plants,
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
    var currentArea by mutableStateOf(AreaType.Plains)
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
    fun isEmpty() = stateMap.isEmpty()
    fun isNotEmpty() = stateMap.isNotEmpty()
    val size get() = stateMap.size
}

class EnemyState(
    hp: Int,
    x: Int,
    y: Int,
    width: Int = 90,
    height: Int = 100,
    val dropInventory: Inventory = Inventory(),
) {
    val id: UUID = UUID.randomUUID()
    var hp by mutableStateOf(hp)
    val maxHp = hp
    var width by mutableStateOf(width)
    var height by mutableStateOf(height)
    var position by mutableStateOf(Offset(x.toFloat(), y.toFloat()))
    var velocity by mutableStateOf(Offset.Zero)
    val speed: Float = 4f
    var cooldown by mutableStateOf(10)

    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = other is EnemyState && other.id == id
}

class PlayerState {
    var hp by mutableStateOf(100)
    var baseDamage by mutableStateOf(1)
    var resourceScanningSpeed by mutableStateOf(0.4f) // TODO: make smaller initially
    var resourceRevealSpeed by mutableStateOf(0.1f) // TODO: make smaller initially
    var resourceMiningSpeed by mutableStateOf(1)
    var statsWindowVisible by mutableStateOf(true)
    var width by mutableStateOf(250)
    var height by mutableStateOf(150)
    var position by mutableStateOf(IntOffset(width / 2, height / 2))
    var infoWidth by mutableStateOf(400)
    var infoHeight by mutableStateOf(200)
    var infoPosition by mutableStateOf(IntOffset(infoWidth / 2 + width + 20, infoHeight / 2))
}

class ResourceFieldState(
    x: Float,
    y: Float,
    width: Int = 300,
    height: Int = 300,
    val inventory: Inventory,
    spawnsRevealed: Boolean = false,
) {
    val id: UUID = UUID.randomUUID()
    var revealProgress by mutableStateOf(if (spawnsRevealed) 1f else 0f)
    val isRevealed get() = revealProgress >= 1f
    var isCollapsed by mutableStateOf(false)
    var width by mutableStateOf(width)
    var height by mutableStateOf(height)
    var position by mutableStateOf(Offset(x, y))
    var stability by mutableStateOf(1f)

    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = other is ResourceFieldState && other.id == id
}
