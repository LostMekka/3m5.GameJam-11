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

fun randomlyPositionedWindowState(windowWidth: Int, windowHeight: Int) =
    WindowState(
        width = windowWidth.dp,
        height = windowHeight.dp,
        position = randomScreenPosition(windowWidth, windowHeight),
    )

fun randomScreenPosition(windowWidth: Int, windowHeight: Int) =
    WindowPosition(
        randomWindowXPosition(windowWidth).dp,
        randomWindowYPosition(windowHeight).dp,
    )

fun randomScreenPositionOffset(windowWidth: Int, windowHeight: Int) =
    Offset(
        randomWindowXPosition(windowWidth),
        randomWindowYPosition(windowHeight),
    )

fun randomWindowYPosition(windowHeight: Int) = Random.nextFloat() * (screenHeight - windowHeight) + windowHeight / 2f
fun randomWindowXPosition(windowWidth: Int) = Random.nextFloat() * (screenWidth - windowWidth) + windowWidth / 2f
