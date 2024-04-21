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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
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
            width = area.size.width.dp,
            height = area.size.height.dp,
            position = WindowPosition(area.position.x.dp, area.position.y.dp),
        ),
    ) {
        if (area.areaType == currentArea) Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) {}
    }
}

@Composable
fun AreaSelectionWindow(state: MovingState, gameState: GameState) {
   val close = { gameState.moving = null }

    Window(
        onCloseRequest = close,
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = state.selectorSize.width.dp,
            height = state.selectorSize.width.dp,
        ),
    ) {
        LaunchedEffect(gameState) {
            state.selectorPosition = Offset(window.x.toFloat(), window.y.toFloat())

            while (true) {
                delay(100)
                if (window.isMinimized) close()

                state.selectorPosition = Offset(window.x.toFloat(), window.y.toFloat())

                val area = overlappingArea(gameState.areas, state.selectorRect)
                if (area == null || gameState.currentArea == area) {
                    state.progress = null
                    continue
                }

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
                    .width((state.selectorSize.width - 40).dp)
                    .height((state.selectorSize.height - 40).dp),
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
    areas.forEach {
        if (movingRect.overlaps(it.value.rect)) return it.key
    }
    return null
}

