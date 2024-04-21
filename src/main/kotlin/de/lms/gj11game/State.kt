package de.lms.gj11game

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import de.lms.gj11game.data.CraftingAction
import de.lms.gj11game.data.CraftingStation
import de.lms.gj11game.data.globalStation
import de.lms.gj11game.helper.*
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
    val player = PlayerState()
    val enemies = mutableStateListOf<EnemyState>()
    val resourceFields = mutableStateListOf<ResourceFieldState>()
    val craftingStation = CraftingStationState(globalStation)
    val firePit = FirePitState()
    val areas = mutableStateMapOf(
        Pair(AreaType.Plains, AreaState(AreaType.Plains, 100f, 100f, unlocked = true)),
        Pair(AreaType.Forest, AreaState(AreaType.Forest, 500f, 100f, unlocked = true)),
        Pair(AreaType.Caves, AreaState(AreaType.Caves, 900f, 100f)),
        Pair(AreaType.Hills, AreaState(AreaType.Hills, 100f, 500f)),
        Pair(AreaType.Mountains, AreaState(AreaType.Mountains, 900f, 500f)),
        Pair(AreaType.Dungeon, AreaState(AreaType.Dungeon, 100f, 900f)),
    )
    var moving by mutableStateOf<MovingState?>(null)
}

class FirePitState {
    var fuelAmount by mutableStateOf(0f)
    var fuelBurnRate by mutableStateOf(0.05f)
}

class CraftingStationState(val station: CraftingStation, unlocked: Boolean = false) {
    var unlocked by mutableStateOf(unlocked)
    var position by mutableStateOf(Rect.randomOnScreen(400f, 400f))
    val actions = station.actions.map { CraftingActionState(it) }
    val innerStations = station.innerStations.map { CraftingStationState(it) }
}

class CraftingActionState(val action: CraftingAction)

class Inventory(vararg resources: ResourcePack) : Iterable<Map.Entry<ResourceType, Int>> {
    private val stateMap = mutableStateMapOf<ResourceType, Int>().also {
        for ((type, amount) in resources) it[type] = amount
    }

    operator fun get(type: ResourceType) = stateMap[type] ?: 0
    operator fun set(type: ResourceType, value: Int) {
        stateMap[type] = value
    }

    override operator fun iterator() = stateMap.iterator()
    operator fun contains(type: ResourceType) = stateMap.containsKey(type)
    operator fun contains(resources: ResourcePack) = this[resources.type] >= resources.amount
    operator fun contains(resources: Inventory) = resources.all { (type, amount) -> this[type] >= amount }
    operator fun plusAssign(other: Inventory) {
        for ((type, amount) in other) this[type] += amount
    }

    operator fun minusAssign(other: Inventory) {
        for ((type, amount) in other) this[type] -= amount
    }

    fun clearEmptySlots() {
        for (type in ResourceType.entries) if (this[type] <= 0) stateMap -= type
    }

    fun isClear() = stateMap.isEmpty()
    fun isNotClear() = stateMap.isNotEmpty()
    fun isEmpty() = stateMap.all { it.value <= 0 }
    fun isNotEmpty() = stateMap.isNotEmpty() && stateMap.any { it.value > 0 }
    val size get() = stateMap.size
    fun toShortString() = if (isEmpty()) "free" else ResourceType.entries
        .filter { this[it] > 0 }
        .joinToString(separator = ",") { "$it:${this[it]}" }
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
    var position by mutableStateOf(Rect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat()))
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
    var interactionRange by mutableStateOf(1f)
    var statsWindowVisible by mutableStateOf(true)
    var position by mutableStateOf(Rect(0f, 0f, 250f, 150f))
    var infoPosition by mutableStateOf(Rect.randomOnScreen(400f, 200f))
}

class ResourceFieldState(
    width: Float = 300f,
    height: Float = 300f,
    x: Float = randomWindowXPosition(width),
    y: Float = randomWindowYPosition(height),
    val inventory: Inventory,
    spawnsRevealed: Boolean = false,
) {
    val id: UUID = UUID.randomUUID()
    var revealProgress by mutableStateOf(if (spawnsRevealed) 1f else 0f)
    val isRevealed get() = revealProgress >= 1f
    var stability by mutableStateOf(1f)
    var isCollapsed by mutableStateOf(false)
    var position by mutableStateOf(Rect.fromMidpoint(x, y, width, height))

    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = other is ResourceFieldState && other.id == id
}

class AreaState(
    val areaType: AreaType,
    x: Float,
    y: Float,
    unlocked: Boolean = false,
) {
    var position by mutableStateOf(Rect(x, y, 200f, 200f))
    var unlocked by mutableStateOf(unlocked)
}

class MovingState {
    var selectorPosition by mutableStateOf(Rect.fromMidpoint(screenWidth / 2f, screenHeight / 2f, 250f, 250f))
    var progress by mutableStateOf<Pair<AreaType, Float>?>(null)
}
