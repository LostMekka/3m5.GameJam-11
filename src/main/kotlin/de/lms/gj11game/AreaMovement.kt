package de.lms.gj11game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import de.lms.gj11game.helper.Rect
import kotlinx.coroutines.delay

@Composable
fun AreaMovementWindows(state: MovingState, gameState: GameState) {
    AreaSelectionWindow(state, gameState)

    for (area in gameState.areas) {
        if (area.value.unlocked) AreaWindow(area.value, gameState.currentArea)
    }
}

@Composable
fun AreaWindow(area: AreaState, currentArea: AreaType) {
    Window(
        title = "${area.areaType}",
        onCloseRequest = {},
        enabled = area.areaType != currentArea,
        state = WindowState(
            width = area.position.width.dp,
            height = area.position.height.dp,
            position = WindowPosition(area.position.x.dp, area.position.y.dp),
        ),
    ) {
        val boxColor = if (area.areaType == currentArea) Color.Gray else Color.Transparent
        Column(
            modifier = Modifier.fillMaxSize().background(boxColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(area.areaType.name)
        }
    }
}

@Composable
fun AreaSelectionWindow(state: MovingState, gameState: GameState) {
    val close = { gameState.moving = null }

    Window(
        onCloseRequest = close,
        state = WindowState(
            position = WindowPosition(state.selectorPosition.x.dp, state.selectorPosition.y.dp),
            width = state.selectorPosition.width.dp,
            height = state.selectorPosition.width.dp,
        ),
    ) {
        LaunchedEffect(gameState) {
            while (true) {
                delay(100)
                if (window.isMinimized) close()

                state.selectorPosition = state.selectorPosition.withPosition(window.x.toFloat(), window.y.toFloat())

                val area = overlappingArea(gameState.areas, state.selectorPosition)
                if (area == null || gameState.currentArea == area) {
                    state.progress = null
                    continue
                }

                if (gameState.areas[area]?.unlocked != true) continue

                var areaProgress = state.progress


                if (areaProgress == null) {
                    state.progress = Pair(area, 0f)
                    continue
                }

                val targetArea = areaProgress.first
                var progress = areaProgress.second

                if (targetArea != area) {
                    state.progress = Pair(area, 0f)
                    continue
                }

                progress += 0.05f
                if (progress >= 1f) {
                    gameState.currentArea = area
                    gameState.moving = null
                    continue
                }

                areaProgress = Pair(area, progress)
                state.progress = areaProgress
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                progress = state.progress?.second ?: 0f,
                modifier = Modifier
                    .width((state.selectorPosition.width - 40).dp)
                    .height((state.selectorPosition.height - 40).dp),
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val progress = state.progress
            if (progress != null && progress.second > 0f)
                Text("Moving to ${progress.first}")
            else
                Button(onClick = close) {
                    Text("Stay in ${gameState.currentArea}")
                }
        }
    }
}

private fun overlappingArea(areas: SnapshotStateMap<AreaType, AreaState>, movingRect: Rect): AreaType? {
    return areas.entries
        .find { it.value.unlocked && movingRect.squaredDistanceTo(it.value.position) <= 0f }
        ?.key
}
