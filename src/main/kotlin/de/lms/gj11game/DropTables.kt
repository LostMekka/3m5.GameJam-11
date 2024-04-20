package de.lms.gj11game

import kotlin.random.Random


data class DropTableEntry(
    val type: ResourceType,
    val minAmount: Int,
    val maxAmount: Int,
    val dropProbability: Double = 1.0,
)

fun DropTableEntry?.toInventory() = Inventory().also { it += this }

fun Iterable<DropTableEntry>?.toInventory() = Inventory().also {
    val dropTable = this ?: emptyList()
    for (entry in dropTable) it += entry
}

operator fun Inventory.plusAssign(entry: DropTableEntry?) {
    if (entry != null && Random.nextDouble() <= entry.dropProbability) {
        this[entry.type] += Random.nextInt(entry.minAmount, entry.maxAmount + 1)
    }
}

val resourceDepositDropTable = mapOf(
    AreaType.Plains to listOf(
        DropTableEntry(ResourceType.Plants, 2, 10),
        DropTableEntry(ResourceType.Wood, 1, 3, 0.5),
        DropTableEntry(ResourceType.Stone, 1, 2, 0.5),
    ),
)
