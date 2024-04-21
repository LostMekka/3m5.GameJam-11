package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import de.lms.gj11game.data.CraftingStationSpecialMechanic
import de.lms.gj11game.helper.playerInInteractionRange
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun CraftingStationView(stationState: CraftingStationState, gameState: GameState, playerIsInRange: Boolean) {
    Column {
        var disableAll = !playerIsInRange
        if (stationState.station.specialMechanic == CraftingStationSpecialMechanic.FirePit) {
            disableAll = gameState.firePit.fuelAmount <= 0
            Text("Fuel: ${(gameState.firePit.fuelAmount * 100).roundToInt()}%")
            Button(
                onClick = { gameState.firePit.fuelAmount += 1f },
                enabled = playerIsInRange && gameState.firePit.fuelAmount < 5f,
            ) {
                Text("Refuel (Wood:1)")
            }
        }

        if (!playerIsInRange) Text("out of range!")
        for (actionState in stationState.actions) CraftingActionView(actionState, gameState, disableAll)
        for (innerStation in stationState.innerStations) {
            CraftingStationUnlockView(innerStation, gameState, disableAll)
        }
    }
}

@Composable
fun CraftingActionView(actionState: CraftingActionState, gameState: GameState, disableAll: Boolean) {
    Button(
        onClick = {
            gameState.inventory -= actionState.action.cost
            actionState.action.action(gameState)
        },
        enabled = !disableAll && actionState.action.cost in gameState.inventory,
    ) {
        Text("${actionState.action.name} (${actionState.action.cost.toShortString()})")
    }
}

@Composable
fun CraftingStationUnlockView(stationState: CraftingStationState, gameState: GameState, disableAll: Boolean = false) {
    if (stationState.unlocked) {
        Window(
            onCloseRequest = {},
            state = WindowState(
                width = stationState.position.width.dp,
                height = stationState.position.height.dp,
                position = WindowPosition(stationState.position.x.dp, stationState.position.y.dp),
            ),
            title = stationState.station.name,
        ) {
            LaunchedEffect(key1 = stationState) {
                while (true) {
                    delay(100)
                    stationState.position = stationState.position.withPosition(window.x.toFloat(), window.y.toFloat())
                    if (window.isMinimized) window.isMinimized = false
                }
            }
            val inRange = gameState.playerInInteractionRange(stationState.position)
            CraftingStationView(stationState, gameState, inRange)
        }
    } else {
        Button(
            onClick = {
                gameState.inventory -= stationState.station.cost
                stationState.unlocked = true
            },
            enabled = !disableAll && stationState.station.cost in gameState.inventory,
        ) {
            Text("Unlock ${stationState.station.name} (${stationState.station.cost.toShortString()})")
        }
    }
}
