package de.lms.gj11game.data

import de.lms.gj11game.AreaType
import de.lms.gj11game.Inventory
import de.lms.gj11game.ResourceType
import kotlin.math.roundToInt
import kotlin.random.Random


data class LootTableEntry(
    val type: ResourceType,
    val minAmount: Int,
    val maxAmount: Int,
    val dropProbability: Double = 1.0,
)

fun LootTableEntry?.toInventory(multiplier: Float = 1f) = Inventory().also { it.add(this, multiplier) }

fun Iterable<LootTableEntry>?.toInventory(multiplier: Float = 1f) = Inventory().also {
    val dropTable = this ?: emptyList()
    for (entry in dropTable) it.add(entry, multiplier)
}

fun Inventory.add(entry: LootTableEntry?, multiplier: Float = 1f) {
    if (entry != null && Random.nextDouble() <= entry.dropProbability) {
        val min = entry.minAmount.toFloat()
        val max = entry.maxAmount.toFloat()
        val amount = ((Random.nextFloat() * (max - min) + min) * multiplier).roundToInt()
        if (amount > 0) this[entry.type] += amount
    }
}

val resourceDepositDropTable = mapOf(
    AreaType.Plains to listOf(
        LootTableEntry(ResourceType.Plants, 2, 10),
        LootTableEntry(ResourceType.Wood, 1, 4, 0.7),
        LootTableEntry(ResourceType.Stone, 1, 4, 0.5),
    ),
    AreaType.Forest to listOf(
        LootTableEntry(ResourceType.Wood, 5, 25),
        LootTableEntry(ResourceType.Plants, 1, 3, 0.2),
        LootTableEntry(ResourceType.Stone, 1, 2, 0.3),
    ),
    AreaType.Mountains to listOf(
        LootTableEntry(ResourceType.Stone, 20, 100),
        LootTableEntry(ResourceType.Plants, 1, 3, 0.2),
        LootTableEntry(ResourceType.Wood, 5, 15, 0.1),
    ),
)
