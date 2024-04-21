package de.lms.gj11game.data

import de.lms.gj11game.AreaType
import de.lms.gj11game.Inventory
import de.lms.gj11game.ResourceType
import kotlin.random.Random


data class LootTableEntry(
    val type: ResourceType,
    val minAmount: Int,
    val maxAmount: Int,
    val dropProbability: Double = 1.0,
)

fun LootTableEntry?.toInventory() = Inventory().also { it += this }

fun Iterable<LootTableEntry>?.toInventory() = Inventory().also {
    val dropTable = this ?: emptyList()
    for (entry in dropTable) it += entry
}

operator fun Inventory.plusAssign(entry: LootTableEntry?) {
    if (entry != null && Random.nextDouble() <= entry.dropProbability) {
        val amount = Random.nextInt(entry.minAmount, entry.maxAmount + 1)
        if (amount > 0) this[entry.type] += amount
    }
}

val resourceDepositDropTable = mapOf(
    AreaType.Plains to listOf(
        LootTableEntry(ResourceType.Plants, 2, 10),
        LootTableEntry(ResourceType.Wood, 1, 4, 0.7),
        LootTableEntry(ResourceType.Stone, 1, 2, 0.5),
    ),
    AreaType.Forest to listOf(
        LootTableEntry(ResourceType.Plants, 1, 3, 0.2),
        LootTableEntry(ResourceType.Wood, 3, 10),
        LootTableEntry(ResourceType.Stone, 1, 2, 0.3),
    )
)
