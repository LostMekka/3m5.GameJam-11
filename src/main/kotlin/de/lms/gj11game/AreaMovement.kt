package de.lms.gj11game

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import de.lms.gj11game.data.areaBackgrounds
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
        alwaysOnTop = true,
        onCloseRequest = {},
        enabled = area.areaType != currentArea,
        state = WindowState(
            position = WindowPosition(area.position.x.dp, area.position.y.dp),
            width = area.position.width.dp,
            height = area.position.height.dp,
        ),
    ) {
        LaunchedEffect(area) {
            while (true) {
                delay(100)
                area.position = area.position.withPosition(window.x.toFloat(), window.y.toFloat())
            }
        }

        areaBackgrounds[area.areaType]?.let { background ->
            Image(
                painter = painterResource(background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                colorFilter = if (area.areaType == currentArea) ColorFilter.tint(Color.White, BlendMode.Saturation) else null,
            )
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                area.areaType.name,
                fontSize = TextUnit(20f, TextUnitType.Sp),
                modifier = Modifier
                    .background(Color.LightGray.copy(alpha = 0.8f))
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun AreaSelectionWindow(state: MovingState, gameState: GameState) {
    val close = { gameState.moving = null }

    Window(
        alwaysOnTop = true,
        onCloseRequest = close,
        state = WindowState(
            position = WindowPosition(state.position.x.dp, state.position.y.dp),
            width = state.position.width.dp,
            height = state.position.width.dp,
        ),
    ) {
        LaunchedEffect(gameState) {
            while (true) {
                delay(100)
                if (window.isMinimized) close()

                state.position = state.position.withPosition(window.x.toFloat(), window.y.toFloat())

                processAreaMovement(state, gameState)
            }
        }

        Image(
            painter = painterResource("drawable/boots.jpeg"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                progress = state.progress?.second ?: 0f,
                modifier = Modifier
                    .width((state.position.width - 40).dp)
                    .height((state.position.height - 40).dp),
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


fun processAreaMovement(state: MovingState, gameState: GameState) {
    val area = overlappingArea(gameState.areas, state.position)
    if (area == null || gameState.currentArea == area) {
        state.progress = null
        return
    }

    if (gameState.areas[area]?.unlocked != true) return

    var areaProgress = state.progress


    if (areaProgress == null) {
        state.progress = Pair(area, 0f)
        return
    }

    val targetArea = areaProgress.first
    var progress = areaProgress.second

    if (targetArea != area) {
        state.progress = Pair(area, 0f)
        return
    }

    progress += 0.05f
    if (progress >= 1f) {
        gameState.currentArea = area
        gameState.enemies.clear()
        gameState.moving = null
        return
    }

    areaProgress = Pair(area, progress)
    state.progress = areaProgress
}
