package de.lms.gj11game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay
import kotlin.math.sqrt

@Composable
fun EnemyWindows(state: GameState) {
    for (enemy in state.enemies) EnemyWindow(enemy, state.player) {
        state.enemies -= enemy
        if (enemy.dropInventory.isNotEmpty()) {
            state.resourceFields += ResourceFieldState(
                x = enemy.position.x,
                y = enemy.position.y,
                inventory = enemy.dropInventory,
                spawnsRevealed = true,
            )
        }
    }
}

@Composable
fun EnemyWindow(state: EnemyState, player: PlayerState, onDeath: () -> Unit) {
    val onClick = {
        state.hp -= player.baseDamage
        if (state.hp <= 0) onDeath()
    }

    val health = "${state.hp} / ${state.maxHp} HP"
    Window(
        onCloseRequest = onClick,
        state = WindowState(
            position = WindowPosition(
                (state.position.x - state.width / 2).dp,
                (state.position.y - state.height / 2).dp,
            ),
            width = state.width.dp,
            height = state.height.dp,
        ),
        resizable = false,
        title = "Enemy $health",
    ) {
        LaunchedEffect(state) {
            while (true) {
                delay(100)
                if (window.isMinimized) {
                    onClick()
                    window.isMinimized = false
                }
            }
        }

        Column {
            Text(health)
            Button(onClick = onClick) {
                Text("Attack")
            }
        }
    }
}

fun moveEnemies(state: GameState) {
    followPlayer(
        player = state.player,
        enemies = state.enemies,
    )
    flockEnemies(state.enemies)
    state.enemies.forEach { enemy ->
        enemy.position += Offset(enemy.velocity.x, enemy.velocity.y)
    }
}


fun followPlayer(player: PlayerState, enemies: SnapshotStateList<EnemyState>) {
    enemies.forEach { enemy ->
        enemy.velocity += (Offset(player.position.x.toFloat(), player.position.y.toFloat()) - enemy.position)
            .normalize(2f)
        enemy.velocity = enemy.velocity.normalizeCap(enemy.speed)
    }
}

fun flockEnemies(enemies: SnapshotStateList<EnemyState>) {
    enemies.forEach { enemy ->
        enemy.velocity +=
            computeAlignment(enemy, enemies) + computeCohesion(enemy, enemies) + computeSeparation(enemy, enemies)
        enemy.velocity = enemy.velocity.normalizeCap(enemy.speed)
    }
}

fun computeAlignment(me: EnemyState, enemies: SnapshotStateList<EnemyState>): Offset {
    var v = Offset.Zero
    var count = 0
    enemies.forEach { other ->
        if (me != other && me.position.distance(other.position) < 150) {
            v += Offset(other.velocity.x, other.velocity.y)
            count++
        }
    }

    if (v == Offset.Zero) return Offset.Zero

    return v.normalize()
}

fun computeCohesion(me: EnemyState, enemies: SnapshotStateList<EnemyState>): Offset {
    var v = Offset.Zero

    var count = 0
    enemies.forEach { other ->
        if (me != other && me.position.distance(other.position) < 150) {
            val diff = other.position - me.position
            v += Offset(diff.x.toFloat(), diff.y.toFloat())
            count++
        }
    }

    if (count == 0) return Offset.Zero

    v = v.times(1f / count)
    v = Offset(v.x - me.position.x, v.y - me.position.y)

    return v.normalize()
}

fun computeSeparation(me: EnemyState, enemies: SnapshotStateList<EnemyState>): Offset {
    var v = Offset.Zero

    var count = 0
    enemies.forEach { other ->
        if (me != other && me.position.distance(other.position) < 150) {
            v += Offset(other.position.x.toFloat(), other.position.y.toFloat())
            count++
        }
    }

    if (count == 0) return Offset.Zero

    v = v.times(1f / count)
    v = Offset(v.x - me.position.x, v.y - me.position.y)

    return v.normalize(-1f)
}

private fun Offset.normalize(targetLength: Float = 1f) = this.times(targetLength / this.length)
private fun Offset.normalizeCap(targetLength: Float = 1f): Offset {
    val len = this.length
    if (len <= targetLength) return this
    return this.times(targetLength / length)
}

private val Offset.length
    get() = sqrt(x * x + y * y)

fun Offset.distance(other: Offset): Float = (this - other).length
