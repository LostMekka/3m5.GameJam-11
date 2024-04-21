package de.lms.gj11game.data

import de.lms.gj11game.AreaType
import de.lms.gj11game.ResourceType

val spawnTable = mapOf(
    AreaType.Plains to SpawnInfo(
        enemyLimit = 5,
        enemies = listOf(
            SpawnEnemyInfo(
                hp = 10,
                dropTable = listOf(
                    LootTableEntry(ResourceType.Meat, 0, 3),
                    LootTableEntry(ResourceType.Bones, 0, 3),
                ),
            ),
            SpawnEnemyInfo(
                hp = 10,
                dropTable = listOf(
                    LootTableEntry(ResourceType.Meat, 0, 3),
                    LootTableEntry(ResourceType.Bones, 0, 2),
                ),
                speed = 4.5f,
            ),
        ),
    ),
    AreaType.Forest to SpawnInfo(
        enemyLimit = 7,
        enemies = listOf(
            SpawnEnemyInfo(
                hp = 15,
                dropTable = listOf(
                    LootTableEntry(ResourceType.Meat, 0, 5),
                    LootTableEntry(ResourceType.Bones, 0, 3),
                ),
            ),
            SpawnEnemyInfo(
                hp = 20,
                dropTable = listOf(
                    LootTableEntry(ResourceType.Wood, 3, 8),
                ),
                speed = 3f,
            ),
        ),
    ),
    AreaType.Caves to SpawnInfo(
        enemyLimit = 10,
        enemies = listOf(
            SpawnEnemyInfo(
                hp = 5,
                dropTable = listOf(
                    LootTableEntry(ResourceType.Meat, 0, 1),
                    LootTableEntry(ResourceType.Bones, 0, 1),
                    LootTableEntry(ResourceType.Stone, 0, 1, 0.3),
                ),
                speed = 5f,
                evasion = 0.4f,
            ),
        ),
    ),
    AreaType.Mountains to SpawnInfo(
        enemyLimit = 6,
        enemies = listOf(
            SpawnEnemyInfo(
                hp = 30,
                dropTable = listOf(
                    LootTableEntry(ResourceType.Stone, 3, 6),
                    LootTableEntry(ResourceType.Iron, 0, 1, 0.3),
                ),
            )
        )
    )
)

data class SpawnInfo(
    val enemyLimit: Int,
    val enemies: List<SpawnEnemyInfo>,
)

data class SpawnEnemyInfo(
    val hp: Int,
    val dropTable: List<LootTableEntry>,
    val speed: Float? = null,
    val evasion: Float? = null,
)
