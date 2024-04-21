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
    val specialMechanic: CraftingStationSpecialMechanic? = null,
    val actions: List<CraftingAction> = emptyList(),
    val innerStations: List<CraftingStation> = emptyList(),
)

enum class CraftingStationSpecialMechanic {
    FirePit,
}

val globalStation = CraftingStation(
    name = "Crafting",
    cost = Inventory(Plants * 10),
    innerStations = listOf(
        CraftingStation(
            name = "Fire Pit",
            cost = Inventory(Wood * 3, Stone * 2),
            specialMechanic = CraftingStationSpecialMechanic.FirePit,
            actions = listOf(
                CraftingAction("Herbal Tea +1HP", Inventory(Plants * 1)) { player.hp += 1 },
                CraftingAction("Steak +3HP", Inventory(Meat * 1)) { player.hp += 3 },
            ),
        ),
        CraftingStation(
            name = "Workbench",
            cost = Inventory(Wood * 10, Bones * 10),
            actions = listOf(
                CraftingAction("Health potion +6HP", Inventory(Plants * 5)) { player.hp += 6 },
                CraftingAction("Health potion +25HP", Inventory(Plants * 20)) { player.hp += 25 },
            ),
        ),
    )
)
