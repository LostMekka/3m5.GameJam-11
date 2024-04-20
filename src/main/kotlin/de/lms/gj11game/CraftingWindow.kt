package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
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
fun CraftingStationView(stationState: CraftingStationState, gameState: GameState) {
    Column {
        for (actionState in stationState.actions) CraftingActionView(actionState, gameState)
        for (innerStation in stationState.innerStations) {
            CraftingStationUnlockView(innerStation, gameState)
        }
    }
}

@Composable
fun CraftingActionView(actionState: CraftingActionState, gameState: GameState) {
    if (actionState.visible) Button(
        onClick = {
            gameState.inventory -= actionState.action.cost
            actionState.action.action(gameState)
        },
        enabled = actionState.action.cost in gameState.inventory,
    ) {
        Text("${actionState.action.name} (${actionState.action.cost.toShortString()})")
    }
}

@Composable
fun CraftingStationUnlockView(stationState: CraftingStationState, gameState: GameState) {
    if (stationState.state == ActionButtonState.Unlocked) {
        Window(
            onCloseRequest = {},
            state = WindowState(
                width = stationState.width.dp,
                height = stationState.height.dp,
                position = WindowPosition(
                    (stationState.position.x - stationState.width / 2).dp,
                    (stationState.position.y - stationState.height / 2).dp
                ),
            ),
            title = stationState.station.name,
        ) {
            LaunchedEffect(key1 = stationState) {
                while (true) {
                    delay(100)
                    stationState.position =
                        Offset(window.x + stationState.width / 2f, window.y + stationState.height / 2f)
                    if (window.isMinimized) {
                        window.toolkit.beep()
                        window.isMinimized = false
                    }
                }
            }
            CraftingStationView(stationState, gameState)
        }
    } else {
        if (stationState.state == ActionButtonState.Visible) Button(
            onClick = {
                gameState.inventory -= stationState.station.cost
                stationState.state = ActionButtonState.Unlocked
            },
            enabled = stationState.station.cost in gameState.inventory,
        ) {
            Text("Unlock ${stationState.station.name} (${stationState.station.cost.toShortString()})")
        }
    }
}