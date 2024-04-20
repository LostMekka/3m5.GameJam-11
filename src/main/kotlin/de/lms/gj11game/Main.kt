package de.lms.gj11game

import androidx.compose.runtime.*
import androidx.compose.ui.window.application

fun main() = application {
    val state = remember { GameState() }
    MainWindow(state, this)
    LaunchedEffect(key1 = this) { mainLoop(state) }
}
