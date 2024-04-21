package de.lms.gj11game

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import de.lms.gj11game.data.CraftingAction
import de.lms.gj11game.data.CraftingStation
import de.lms.gj11game.data.globalStation
import de.lms.gj11game.helper.randomScreenPositionOffset
import de.lms.gj11game.helper.randomWindowXPosition
import de.lms.gj11game.helper.randomWindowYPosition
import androidx.compose.ui.unit.IntSize
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

enum class ActionButtonState { Hidden, Visible, Unlocked }

class CraftingStationState(val station: CraftingStation, state: ActionButtonState = ActionButtonState.Hidden) {
    var state by mutableStateOf(state)
    var width by mutableStateOf(400)
    var height by mutableStateOf(400)
    var position by mutableStateOf(randomScreenPositionOffset(width, height))
    val actions = station.actions.map { CraftingActionState(it) }
    val innerStations = station.innerStations.map { CraftingStationState(it) }
}

class CraftingActionState(val action: CraftingAction, visible: Boolean = false) {
    var visible by mutableStateOf(visible)
}

class Inventory(vararg resources: ResourcePack): Iterable<Map.Entry<ResourceType, Int>> {
    private val stateMap = mutableStateMapOf<ResourceType, Int>().also {
        for ((type, amount) in resources) it[type] = amount
    }
    operator fun get(type: ResourceType) = stateMap[type] ?: 0
    operator fun set(type: ResourceType, value: Int) { stateMap[type] = value }
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
    fun isEmpty() = stateMap.isEmpty()
    fun isNotEmpty() = stateMap.isNotEmpty()
    val size get() = stateMap.size
    fun toShortString() = if (isEmpty()) "free" else ResourceType.entries
        .filter { it in this }
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
    var infoPosition by mutableStateOf(randomScreenPositionOffset(infoWidth, infoHeight))
}

class ResourceFieldState(
    width: Int = 300,
    height: Int = 300,
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
    var width by mutableStateOf(width)
    var height by mutableStateOf(height)
    var position by mutableStateOf(Offset(x, y))

    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = other is ResourceFieldState && other.id == id
}

class AreaState(
    val areaType: AreaType,
    x: Float,
    y: Float,
    unlocked: Boolean = false,
) {
    var position by mutableStateOf(Offset(x, y))
    var unlocked by mutableStateOf(unlocked)
    val size = IntSize(200, 200)
    val rect get() = Rect(position, size.width, size.height)
}

class MovingState() {
    var selectorPosition by mutableStateOf(Offset.Unspecified)
    val selectorSize = IntSize(150, 150)
    val selectorRect get(): Rect = Rect(selectorPosition, selectorSize.width, selectorSize.height)

    var progress by mutableStateOf<Pair<AreaType, Float>?>(null)
}

fun Rect(selectorPosition: Offset, width: Int, height: Int): Rect =
    Rect(
        left = selectorPosition.x - width / 2,
        right = selectorPosition.x + width / 2,
        top = selectorPosition.y - height / 2,
        bottom = selectorPosition.y + height / 2,
    )
