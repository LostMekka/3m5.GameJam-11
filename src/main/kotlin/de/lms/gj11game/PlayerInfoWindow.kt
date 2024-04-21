package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay


@Composable
fun PlayerInfoWindow(state: PlayerState) {
    if (!state.statsWindowVisible) return
    Window(
        onCloseRequest = { state.statsWindowVisible = false },
        state = WindowState(
            width = state.infoPosition.width.dp,
            height = state.infoPosition.height.dp,
            position = WindowPosition(state.infoPosition.x.dp, state.infoPosition.y.dp),
        ),
        resizable = false,
        title = "Character Information",
    ) {
        LaunchedEffect(key1 = state) {
            while (true) {
                delay(100)
                state.infoPosition = state.infoPosition.withPosition(window.x.toFloat(), window.y.toFloat())
                if (window.isMinimized) {
                    state.statsWindowVisible = false
                }
            }
        }
        Column {
            Text("HP: ${state.hp}")
            Text("Base damage: ${state.baseDamage}")
            Text("Resource scanning speed: ${state.resourceScanningSpeed}")
            Text("Resource scanning stacks: ${state.resourceScanningStacks}")
            Text("Resource reveal speed: ${state.resourceRevealSpeed}")
            Text("Resource looting amount: ${state.resourceLootingAmount}")
            Text("Enemy loot multiplier: ${state.enemyLootMultiplier}")
            Text("Interaction range: ${state.interactionRange}")
        }
    }
}
