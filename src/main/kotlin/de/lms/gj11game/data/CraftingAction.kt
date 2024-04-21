package de.lms.gj11game.data

import de.lms.gj11game.*
import de.lms.gj11game.ResourceType.*

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
    val homeArea: AreaType? = null,
    val onUnlock: GameState.() -> Unit = {},
    val actions: List<CraftingAction> = emptyList(),
    val upgrades: List<CraftingUpgrade> = emptyList(),
    val innerStations: List<CraftingStation> = emptyList(),
)

sealed class CraftingStationSpecialMechanic {
    data object FirePit : CraftingStationSpecialMechanic()
    data class ResourceGenerator(
        val stateSelector: GameState.() -> ResourceGeneratorState,
    ) : CraftingStationSpecialMechanic()
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

private val constructionGuildStation = CraftingStation(
    name = "Construction Guild",
    cost = Inventory(Wood * 100, Stone * 200, Bones * 50),
    innerStations = listOf(
        CraftingStation(
            name = "Farm",
            cost = Inventory(Wood * 200, Stone * 50, Bones * 50),
            specialMechanic = CraftingStationSpecialMechanic.ResourceGenerator { farm },
            homeArea = AreaType.Plains,
            upgrades = listOf(
                CraftingUpgrade(
                    name = "Farming Speed",
                    levels = listOf(
                        CraftingUpgradeLevel(Inventory(Bones * 80)) { farm.generationSpeed *= 2f },
                        CraftingUpgradeLevel(Inventory(Bones * 150)) { farm.generationSpeed *= 2f },
                        CraftingUpgradeLevel(Inventory(Bones * 400)) { farm.generationSpeed *= 2f },
                        CraftingUpgradeLevel(Inventory(Bones * 1200)) { farm.generationSpeed *= 2f },
                    ),
                ),
                CraftingUpgrade(
                    name = "Stockpile",
                    levels = listOf(
                        CraftingUpgradeLevel(Inventory(Wood * 100)) { farm.maxAmount *= 4 },
                        CraftingUpgradeLevel(Inventory(Wood * 200)) { farm.maxAmount *= 4 },
                        CraftingUpgradeLevel(Inventory(Wood * 400)) { farm.maxAmount *= 4 },
                        CraftingUpgradeLevel(Inventory(Wood * 800)) { farm.maxAmount *= 4 },
                    ),
                ),
            ),
        ),
        CraftingStation(
            name = "Sawmill",
            cost = Inventory(Wood * 200, Stone * 200),
            specialMechanic = CraftingStationSpecialMechanic.ResourceGenerator { sawmill },
            homeArea = AreaType.Forest,
            upgrades = listOf(
                CraftingUpgrade(
                    name = "Logging Speed",
                    levels = listOf(
                        CraftingUpgradeLevel(Inventory(Wood * 400, Stone * 200)) { sawmill.generationSpeed *= 2f },
                        CraftingUpgradeLevel(Inventory(Wood * 800, Stone * 200)) { sawmill.generationSpeed *= 2f },
                        CraftingUpgradeLevel(Inventory(Wood * 1600, Stone * 200)) { sawmill.generationSpeed *= 2f },
                        CraftingUpgradeLevel(Inventory(Wood * 3200, Stone * 200)) { sawmill.generationSpeed *= 2f },
                    ),
                ),
                CraftingUpgrade(
                    name = "Stockpile",
                    levels = listOf(
                        CraftingUpgradeLevel(Inventory(Stone * 200)) { sawmill.maxAmount *= 4 },
                        CraftingUpgradeLevel(Inventory(Stone * 400)) { sawmill.maxAmount *= 4 },
                        CraftingUpgradeLevel(Inventory(Stone * 800)) { sawmill.maxAmount *= 4 },
                        CraftingUpgradeLevel(Inventory(Stone * 1600)) { sawmill.maxAmount *= 4 },
                    ),
                ),
            ),
        ),
        CraftingStation(
            name = "Quarry",
            cost = Inventory(Wood * 200, Stone * 200, Plants * 500),
            specialMechanic = CraftingStationSpecialMechanic.ResourceGenerator { quarry },
            homeArea = AreaType.Mountains,
            upgrades = listOf(
                CraftingUpgrade(
                    name = "Mining Speed",
                    levels = listOf(
                        CraftingUpgradeLevel(Inventory(Wood * 400, Stone * 200)) { quarry.generationSpeed *= 2f },
                        CraftingUpgradeLevel(Inventory(Wood * 800, Stone * 200)) { quarry.generationSpeed *= 2f },
                        CraftingUpgradeLevel(Inventory(Wood * 1600, Stone * 200)) { quarry.generationSpeed *= 2f },
                        CraftingUpgradeLevel(Inventory(Wood * 3200, Stone * 200)) { quarry.generationSpeed *= 2f },
                    ),
                ),
                CraftingUpgrade(
                    name = "Stockpile",
                    levels = listOf(
                        CraftingUpgradeLevel(Inventory(Wood * 200)) { quarry.maxAmount *= 4 },
                        CraftingUpgradeLevel(Inventory(Wood * 400)) { quarry.maxAmount *= 4 },
                        CraftingUpgradeLevel(Inventory(Wood * 800)) { quarry.maxAmount *= 4 },
                        CraftingUpgradeLevel(Inventory(Wood * 1600)) { quarry.maxAmount *= 4 },
                    ),
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
                        CraftingUpgradeLevel(Inventory(Plants * 20, Wood * 20, Stone * 5)) { player.resourceLootingAmount = 2 },
                        CraftingUpgradeLevel(Inventory(Plants * 100, Wood * 100, Stone * 100)) { player.resourceLootingAmount = 5 },
                        CraftingUpgradeLevel(Inventory(Plants * 400, Wood * 400, Stone * 400)) { player.resourceLootingAmount = 20 },
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
                CraftingUpgrade(
                    name = "Player Damage",
                    levels = listOf(
                        CraftingUpgradeLevel(Inventory(Wood * 100, Bones * 50)) { player.baseDamage++ },
                        CraftingUpgradeLevel(Inventory(Wood * 200, Bones * 100)) { player.baseDamage++ },
                        CraftingUpgradeLevel(Inventory(Wood * 500, Bones * 200)) { player.baseDamage++ },
                        CraftingUpgradeLevel(Inventory(Wood * 1000, Bones * 400)) { player.baseDamage++ },
                    ),
                ),
            ),
            innerStations = listOf(
                scoutingTowerStation,
                constructionGuildStation,
            ),
        ),
    ),
)
