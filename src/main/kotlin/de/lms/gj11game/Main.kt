package de.lms.gj11game

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val lostPreviously = remember { mutableStateOf(false) }
    val state = remember { mutableStateOf<GameState?>(null) }

    state.value?.also {
        MainWindow(
            it,
            this,
            onGameOver = {
                state.value = null
                lostPreviously.value = true
            },
        )
        LaunchedEffect(key1 = state.value) { mainLoop(it) }

        return@application
    }


    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 500.dp,
            height = 300.dp,
        ),
        title = "This is a Game! Don't let anyone tell you otherwise!",
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                fontSize = TextUnit(38f, TextUnitType.Sp),
                textAlign = TextAlign.Center,
                text = "Windows Defender",
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { state.value = GameState() }) {
                Text(if (lostPreviously.value) "Restart" else "Start")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = ::exitApplication) {
                Text("Quit")
            }
        }
    }
}
