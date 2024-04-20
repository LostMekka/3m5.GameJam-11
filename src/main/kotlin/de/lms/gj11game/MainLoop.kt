package de.lms.gj11game

import kotlinx.coroutines.delay
import kotlin.random.Random

const val frameDelay = 100L
// TODO: find out whether state changes from within here are actually thread safe!
suspend fun mainLoop(state: GameState) {
    while (true) {
        delay(frameDelay)
        val dt = frameDelay / 1000f

        spawnEnemies(state)
        updateResourceScanning(state, dt)
    }
}

private fun spawnEnemies(state: GameState) {
    if (state.enemies.size < 5 && Random.nextDouble() > 0.9) {
        state.enemies += EnemyState(
            hp = 10,
            x = Random.nextInt(300, 1000),
            y = Random.nextInt(300, 600),
        )
    }
}

private fun updateResourceScanning(state: GameState, dt: Float) {
    state.resourceScanningProgress = state.resourceScanningProgress?.let {
        val newProgress = it + state.player.resourceScanningSpeed * dt
        if (newProgress >= 1) {
            state.resourceFields += ResourceFieldState(
                x = Random.nextInt(300, 1000),
                y = Random.nextInt(300, 600),
                amount = 10 + Random.nextInt(20),
            )
            null
        } else newProgress
    }
}
