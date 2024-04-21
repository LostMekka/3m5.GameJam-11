package de.lms.gj11game.helper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.awt.Dimension
import java.awt.Toolkit
import kotlin.random.Random

val screenSize: Dimension by lazy { Toolkit.getDefaultToolkit().screenSize }
val screenWidth by lazy { screenSize.width }
val screenHeight by lazy { screenSize.height }

fun randomlyPositionedWindowState(windowWidth: Float, windowHeight: Float) =
    WindowState(
        width = windowWidth.dp,
        height = windowHeight.dp,
        position = randomScreenPosition(windowWidth, windowHeight),
    )

fun randomScreenPosition(windowWidth: Float, windowHeight: Float) =
    WindowPosition(
        randomWindowXPosition(windowWidth).dp,
        randomWindowYPosition(windowHeight).dp,
    )

fun randomScreenPositionOffset(windowWidth: Float, windowHeight: Float) =
    Offset(
        randomWindowXPosition(windowWidth),
        randomWindowYPosition(windowHeight),
    )

fun randomWindowXPosition(windowWidth: Float) = Random.nextFloat() * (screenWidth - windowWidth) + windowWidth / 2f
fun randomWindowYPosition(windowHeight: Float) = Random.nextFloat() * (screenHeight - windowHeight) + windowHeight / 2f
