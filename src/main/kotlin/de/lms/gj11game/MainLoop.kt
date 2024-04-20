package de.lms.gj11game

import kotlinx.coroutines.delay
import kotlin.random.Random


// TODO: find out whether state changes from within here are actually thread safe!
suspend fun mainLoop(state: GameState) {
    while (true) {
        delay(100)

        state.score++

        if (state.enemies.size < 5 && Random.nextDouble() > 0.9) {
            state.enemies += EnemyState(
                hp = 100,
                x = Random.nextInt(100, 800),
                y = Random.nextInt(100, 400),
            )
        }
    }
}
