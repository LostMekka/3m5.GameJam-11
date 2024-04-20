package de.lms.gj11game.data

import de.lms.gj11game.GameState
import de.lms.gj11game.Inventory
import de.lms.gj11game.ResourceType.*
import de.lms.gj11game.times

data class CraftingAction(
    val name: String,
    val cost: Inventory,
    val action: GameState.() -> Unit,
)

data class CraftingStation(
    val name: String,
    val cost: Inventory,
    val actions: List<CraftingAction> = emptyList(),
    val innerStations: List<CraftingStation> = emptyList(),
)

val globalStation = CraftingStation(
    name = "Crafting",
    cost = Inventory(Plants * 10),
    innerStations = listOf(
        CraftingStation(
            name = "Workbench",
            cost = Inventory(Wood * 5),
            actions = listOf(
                CraftingAction("Health potion +5", Inventory(Plants * 5)) { player.hp += 5 },
                CraftingAction("Health potion +25", Inventory(Plants * 20)) { player.hp += 25 },
            ),
        ),
    )
)
