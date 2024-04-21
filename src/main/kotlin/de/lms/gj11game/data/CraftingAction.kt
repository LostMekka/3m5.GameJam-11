package de.lms.gj11game.data

import de.lms.gj11game.AreaType
import de.lms.gj11game.GameState
import de.lms.gj11game.Inventory
import de.lms.gj11game.ResourceType.*
import de.lms.gj11game.times

data class CraftingAction(
    val name: String,
    val cost: Inventory,
    val action: GameState.() -> Unit,
)

data class CraftingUpgrade(
    val name: String,
    val levels: List<CraftingUpgradeLevel>,
)

data class CraftingUpgradeLevel(
    val cost: Inventory,
    val action: GameState.() -> Unit,
)

data class CraftingStation(
    val name: String,
    val cost: Inventory,
    val specialMechanic: CraftingStationSpecialMechanic? = null,
    val actions: List<CraftingAction> = emptyList(),
    val upgrades: List<CraftingUpgrade> = emptyList(),
    val innerStations: List<CraftingStation> = emptyList(),
)

enum class CraftingStationSpecialMechanic {
    FirePit,
}

private val scoutingTowerStation = CraftingStation(
    name = "Scouting Tower",
    cost = Inventory(Wood * 35, Bones * 10, Stone * 5),
    upgrades = listOf(
        CraftingUpgrade(
            "Resource Scan Speed",
            listOf(
                CraftingUpgradeLevel(
                    cost = Inventory(Plants * 100, Wood * 200, Bones * 10),
                    action = { player.resourceScanningSpeed *= 2f },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Plants * 200, Wood * 500, Bones * 20),
                    action = { player.resourceScanningSpeed *= 2f },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Plants * 300, Wood * 1000, Bones * 50),
                    action = { player.resourceScanningSpeed *= 2f },
                ),
            ),
        ),
        CraftingUpgrade(
            "Resource Scan Stacks",
            listOf(
                CraftingUpgradeLevel(
                    cost = Inventory(Plants * 100, Wood * 200, Bones * 10),
                    action = { player.resourceScanningStacks = 2 },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Plants * 200, Wood * 500, Bones * 20),
                    action = { player.resourceScanningStacks = 4 },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Plants * 300, Wood * 1000, Bones * 50),
                    action = { player.resourceScanningStacks = 8 },
                ),
            ),
        ),
        CraftingUpgrade(
            "Scout Plains",
            listOf(
                CraftingUpgradeLevel(
                    cost = Inventory(Plants * 20, Wood * 5, Bones * 10),
                    action = { areas.getValue(AreaType.Plains).resourceMultiplier *= 2f },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Plants * 100, Wood * 50, Bones * 10),
                    action = { areas.getValue(AreaType.Plains).resourceMultiplier *= 2f },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Plants * 300, Wood * 130, Bones * 40),
                    action = { areas.getValue(AreaType.Plains).resourceMultiplier *= 2f },
                ),
            ),
        ),
        CraftingUpgrade(
            "Scout Forest",
            listOf(
                CraftingUpgradeLevel(
                    cost = Inventory(Wood * 40, Stone * 5, Bones * 10),
                    action = { areas.getValue(AreaType.Forest).resourceMultiplier *= 2f },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Wood * 200, Stone * 50, Bones * 10),
                    action = { areas.getValue(AreaType.Forest).resourceMultiplier *= 2f },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Wood * 500, Stone * 90, Bones * 40),
                    action = { areas.getValue(AreaType.Forest).resourceMultiplier *= 2f },
                ),
            ),
        ),
        CraftingUpgrade(
            "Scout Mountains",
            listOf(
                CraftingUpgradeLevel(
                    cost = Inventory(Wood * 50, Bones * 10),
                    action = { areas.getValue(AreaType.Mountains).unlocked = true },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Wood * 100, Stone * 50, Bones * 10),
                    action = { areas.getValue(AreaType.Mountains).resourceMultiplier *= 2f },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Wood * 200, Stone * 90, Bones * 10),
                    action = { areas.getValue(AreaType.Mountains).resourceMultiplier *= 2f },
                ),
                CraftingUpgradeLevel(
                    cost = Inventory(Wood * 500, Stone * 150, Bones * 40),
                    action = { areas.getValue(AreaType.Mountains).resourceMultiplier *= 2f },
                ),
            ),
        ),
    ),
)

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
            upgrades = listOf(
                CraftingUpgrade(
                    name = "Looting Speed",
                    levels = listOf(
                        CraftingUpgradeLevel(Inventory(Plants * 20, Wood * 20, Stone * 5)) { player.resourceMiningSpeed = 2 },
                        CraftingUpgradeLevel(Inventory(Plants * 100, Wood * 100, Stone * 100)) { player.resourceMiningSpeed = 5 },
                        CraftingUpgradeLevel(Inventory(Plants * 400, Wood * 400, Stone * 400)) { player.resourceMiningSpeed = 20 },
                    ),
                ),
                CraftingUpgrade(
                    name = "Enemy Loot",
                    levels = listOf(
                        CraftingUpgradeLevel(Inventory(Meat * 15, Bones * 10, Plants * 50)) { player.enemyLootMultiplier = 3f },
                        CraftingUpgradeLevel(Inventory(Meat * 50, Bones * 50, Wood * 400)) { player.enemyLootMultiplier = 7f },
                        CraftingUpgradeLevel(Inventory(Meat * 200, Bones * 150, Stone * 500)) { player.enemyLootMultiplier = 13f },
                        CraftingUpgradeLevel(Inventory(Meat * 1000, Bones * 500, Plants * 2000, Wood * 2000, Stone * 2000)) { player.enemyLootMultiplier = 47f },
                    ),
                ),
            ),
            innerStations = listOf(
                scoutingTowerStation,
            ),
        ),
    ),
)
