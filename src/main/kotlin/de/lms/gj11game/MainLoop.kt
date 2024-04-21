package de.lms.gj11game

import de.lms.gj11game.data.resourceDepositDropTable
import de.lms.gj11game.data.spawnTable
import de.lms.gj11game.data.toInventory
import de.lms.gj11game.helper.playerInInteractionRange
import kotlinx.coroutines.delay
import kotlin.random.Random

const val frameDelay = 100L

// TODO: find out whether state changes from within here are actually thread safe!
suspend fun mainLoop(state: GameState) {
    while (true) {
        delay(frameDelay)
        val dt = frameDelay / 1000f

        spawnEnemies(state)
        moveEnemies(state)
        handleDamage(state)
        updateResourceScanning(state, dt)
        updateResourceFields(state, dt)
        updateFirePit(state, dt)
        updateResourceGenerator(state.farm, dt)
        updateResourceGenerator(state.sawmill, dt)
        updateResourceGenerator(state.quarry, dt)
    }
}

@Suppress("SameParameterValue")
private fun updateResourceFields(gameState: GameState, dt: Float) {
    for (resourceState in gameState.resourceFields) {
        if (!resourceState.isRevealed && gameState.playerInInteractionRange(resourceState.position)) {
            resourceState.revealProgress += gameState.player.resourceRevealSpeed * dt
        }
    }
}

@Suppress("SameParameterValue")
private fun updateFirePit(state: GameState, dt: Float) {
    state.firePit.fuelAmount -= state.firePit.fuelBurnRate * dt
    if (state.firePit.fuelAmount < 0f) state.firePit.fuelAmount = 0f
}

@Suppress("SameParameterValue")
private fun updateResourceGenerator(state: ResourceGeneratorState, dt: Float) {
    if (!state.unlocked) return
    state.amount += state.generationSpeed * dt
    if (state.amount > state.maxAmount) state.amount = state.maxAmount.toFloat()
}

private fun spawnEnemies(state: GameState) {
    spawnTable[state.currentArea]?.also { spawnInfo ->
        if (state.enemies.size < spawnInfo.enemyLimit && Random.nextDouble() > 0.9) {
            val which = Random.nextInt(spawnInfo.enemies.size)
            val enemyInfo = spawnInfo.enemies[which]

            state.enemies += EnemyState(
                hp = enemyInfo.hp,
                x = Random.nextInt(300, 1000),
                y = Random.nextInt(300, 600),
                dropInventory = enemyInfo.dropTable.toInventory(state.player.enemyLootMultiplier),
                evasion = enemyInfo.evasion,
                speed = enemyInfo.speed,
            )
        }
    }
}

@Suppress("SameParameterValue")
private fun updateResourceScanning(state: GameState, dt: Float) {
    if (state.resourceScanningStacks <= 0) return
    val newProgress = state.resourceScanningProgress + state.player.resourceScanningSpeed * dt
    if (newProgress >= 1) {
        val multiplier = state.areas[state.currentArea]?.resourceMultiplier ?: 1f
        val inventory = resourceDepositDropTable[state.currentArea].toInventory(multiplier)
        state.resourceFields += ResourceFieldState(inventory = inventory)
        state.resourceScanningProgress = if (state.resourceScanningStacks > 1) newProgress - 1f else 0f
        state.resourceScanningStacks--
    } else {
        state.resourceScanningProgress = newProgress
    }
}

private fun handleDamage(state: GameState) {
    val playerOffset = state.player.position
    state.enemies.forEach {
        if (it.position.squaredDistanceTo(playerOffset) <= 0f) {
            if (it.cooldown == 0) {
                state.player.hp -= 1
                it.cooldown = 10
            } else {
                it.cooldown--
            }
        }
    }
}
