package de.lms.gj11game

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState

@Composable
fun GameOverWindow(
    applicationScope: ApplicationScope,
    onRestart: () -> Unit
) {
    Window(
        onCloseRequest = applicationScope::exitApplication,
        state = rememberWindowState(
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
            Button(onClick = onRestart) {
                Text("Restart")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRestart) {
                Text("Restart")
            }
        }
    }
}
