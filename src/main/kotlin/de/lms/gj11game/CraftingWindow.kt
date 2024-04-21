package de.lms.gj11game

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import de.lms.gj11game.data.CraftingStationSpecialMechanic.*
import de.lms.gj11game.helper.playerInInteractionRange
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun CraftingStationView(stationState: CraftingStationState, gameState: GameState, playerIsInRange: Boolean) {
    Column {
        var disableAll = !playerIsInRange
        when (stationState.station.specialMechanic) {
            FirePit -> {
                disableAll = gameState.firePit.fuelAmount <= 0
                Text("Fuel: ${(gameState.firePit.fuelAmount * 100).roundToInt()}%")
                Button(
                    onClick = { gameState.firePit.fuelAmount += 1f },
                    enabled = playerIsInRange && gameState.firePit.fuelAmount < 5f,
                ) {
                    Text("Refuel (Wood:1)")
                }
            }

            is ResourceGenerator -> {
                val generatorState = stationState.station.specialMechanic.stateSelector(gameState)
                val readyAmount = generatorState.amount.toInt()
                Row {
                    Text("Generating... (${generatorState.resourceType})")
                    Spacer(Modifier.width(5.dp))
                    CircularProgressIndicator(
                        progress = generatorState.amount % 1f,
                        modifier = Modifier.width(20.dp).height(20.dp),
                    )
                }
                Button(
                    onClick = {
                        val lootAmount = minOf(readyAmount, gameState.player.resourceLootingAmount)
                        generatorState.amount -= lootAmount
                        gameState.inventory[generatorState.resourceType] += lootAmount
                    },
                    enabled = playerIsInRange && readyAmount > 0,
                ) {
                    Text("Take (${generatorState.resourceType}:$readyAmount)")
                }
            }

            null -> Unit
        }

        if (!playerIsInRange) Text("out of range!")
        if (stationState.actions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Actions:")
            for (actionState in stationState.actions) CraftingActionView(actionState, gameState, disableAll)
        }
        if (stationState.upgrades.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Upgrades:")
            for (upgradeState in stationState.upgrades) UpgradeActionView(upgradeState, gameState, disableAll)
        }
        if (stationState.innerStations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Unlocks:")
            for (innerStation in stationState.innerStations) {
                CraftingStationUnlockView(innerStation, gameState, disableAll)
            }
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
fun UpgradeActionView(upgradeState: CraftingUpgradeState, gameState: GameState, disableAll: Boolean) {
    val level = upgradeState.currentLevel
    if (level == null) {
        Button(
            onClick = {},
            enabled = false,
        ) {
            Text("${upgradeState.upgrade.name} (MAX LVL)")
        }
    } else {
        Button(
            onClick = {
                gameState.inventory -= level.cost
                upgradeState.currentIndex++
                level.action(gameState)
            },
            enabled = !disableAll && level.cost in gameState.inventory,
        ) {
            Text("${upgradeState.upgrade.name} LVL${upgradeState.currentIndex + 2} (${level.cost.toShortString()})")
        }
    }
}

@Composable
fun CraftingStationUnlockView(stationState: CraftingStationState, gameState: GameState, disableAll: Boolean = false) {
    val validArea = stationState.station.homeArea == null || gameState.currentArea == stationState.station.homeArea
    if (stationState.unlocked) {
        if (validArea) Window(
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
        Button(
            onClick = {},
            enabled = false,
        ) {
            Text("${stationState.station.name} (UNLOCKED)")
        }
    } else {
        Button(
            onClick = {
                gameState.inventory -= stationState.station.cost
                stationState.unlocked = true
                stationState.station.onUnlock(gameState)
                (stationState.station.specialMechanic as? ResourceGenerator)?.let { it.stateSelector(gameState).unlocked = true }
            },
            enabled = validArea && !disableAll && stationState.station.cost in gameState.inventory,
        ) {
            val areaHint = if (validArea) "" else " (only in ${stationState.station.homeArea})"
            Text("Unlock ${stationState.station.name} (${stationState.station.cost.toShortString()})$areaHint")
        }
    }
}
