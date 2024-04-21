package de.lms.gj11game.helper

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.awt.Dimension
import java.awt.Toolkit
import kotlin.random.Random

val screenSize: Dimension by lazy { Toolkit.getDefaultToolkit().screenSize }
val screenWidth by lazy { screenSize.width }
val screenHeight by lazy { screenSize.height }

fun randomlyPositionedWindowState(windowWidth: Float, windowHeight: Float, border: Float = 0f) =
    WindowState(
        width = windowWidth.dp,
        height = windowHeight.dp,
        position = randomScreenPosition(windowWidth, windowHeight, border),
    )

fun randomScreenPosition(windowWidth: Float, windowHeight: Float, border: Float = 0f) =
    WindowPosition(
        randomWindowXPosition(windowWidth, border).dp,
        randomWindowYPosition(windowHeight, border).dp,
    )

fun randomWindowMidXPosition(windowWidth: Float, border: Float = 0f) = randomWindowXPosition(windowWidth, border) + windowWidth / 2f
fun randomWindowMidYPosition(windowHeight: Float, border: Float = 0f) = randomWindowYPosition(windowHeight, border) + windowHeight / 2f
fun randomWindowXPosition(windowWidth: Float, border: Float = 0f) = Random.nextFloat() * (screenWidth - windowWidth - 2 * border) + windowWidth / 2f + border
fun randomWindowYPosition(windowHeight: Float, border: Float = 0f) = Random.nextFloat() * (screenHeight - windowHeight - 2 * border) + windowHeight / 2f + border
