package de.lms.gj11game.helper

import de.lms.gj11game.GameState

fun GameState.playerInInteractionRange(other: Rect): Boolean {
    val h = player.interactionRange
    return player.position squaredDistanceTo other <= h * h
}
