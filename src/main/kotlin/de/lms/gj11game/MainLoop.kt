package de.lms.gj11game

import de.lms.gj11game.data.resourceDepositDropTable
import de.lms.gj11game.data.toInventory
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
        updateFirePit(state, dt)
    }
}

@Suppress("SameParameterValue")
private fun updateFirePit(state: GameState, dt: Float) {
    state.firePit.fuelAmount -= state.firePit.fuelBurnRate * dt
    if (state.firePit.fuelAmount < 0f) state.firePit.fuelAmount = 0f
}

private fun spawnEnemies(state: GameState) {
    if (state.enemies.size < 5 && Random.nextDouble() > 0.9) {
        state.enemies += EnemyState(
            hp = 10,
            x = Random.nextInt(300, 1000),
            y = Random.nextInt(300, 600),
            dropInventory = Inventory(
                ResourceType.Meat * (Random.nextInt(5)),
                ResourceType.Bones * (Random.nextInt(3)),
            ),
        )
    }
}

@Suppress("SameParameterValue")
private fun updateResourceScanning(state: GameState, dt: Float) {
    state.resourceScanningProgress = state.resourceScanningProgress?.let {
        val newProgress = it + state.player.resourceScanningSpeed * dt
        if (newProgress >= 1) {
            state.resourceFields += ResourceFieldState(
                inventory = resourceDepositDropTable[state.currentArea].toInventory(),
            )
            null
        } else newProgress
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
