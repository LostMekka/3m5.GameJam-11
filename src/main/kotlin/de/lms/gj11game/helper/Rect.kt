package de.lms.gj11game.helper

import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

data class Rect(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
) {
    val midX = x + width / 2
    val midY = y + height / 2
    val offset get() = Offset(x, y)
    val midOffset get() = Offset(midX, midY)

    fun translated(x: Float, y: Float) = Rect(this.x + x, this.y + y, width, height)
    fun translated(pos: Offset) = translated(pos.x, pos.y)
    fun withPosition(x: Float, y: Float) = Rect(x, y, width, height)
    fun withPosition(pos: Offset) = withPosition(pos.x, pos.y)
    fun withMidPoint(x: Float, y: Float) = fromMidpoint(x, y, width, height)
    fun withMidPoint(pos: Offset) = withMidPoint(pos.x, pos.y)

    infix fun squaredDistanceTo(other: Rect): Float {
        val dx = maxOf(other.x - x - width, x - other.x - other.width, 0f)
        val dy = maxOf(other.y - y - height, y - other.y - other.height, 0f)
        return dx * dx + dy * dy
    }

    fun isInRangeOf(other: Rect, maxDistance: Float): Boolean {
        return squaredDistanceTo(other) <= maxDistance * maxDistance
    }

    companion object {
        fun fromMidpoint(midX: Float, midY: Float, width: Float, height: Float) =
            Rect(midX - width / 2f, midY - height / 2f, width, height)
        fun randomOnScreen(width: Float, height: Float, border: Float = 0f) =
            Rect(
                randomWindowXPosition(width, border),
                randomWindowYPosition(height, border),
                width,
                height,
            )
    }
}
