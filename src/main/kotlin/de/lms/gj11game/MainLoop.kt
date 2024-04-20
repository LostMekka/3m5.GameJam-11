package de.lms.gj11game

import kotlinx.coroutines.delay


// TODO: find out whether state changes from within here are actually thread safe!
suspend fun mainLoop(state: GameState) {
    while (true) {
        delay(100)
        // TODO
        state.score++
    }
}
