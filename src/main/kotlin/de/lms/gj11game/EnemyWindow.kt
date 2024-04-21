package de.lms.gj11game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
        state.resourceFields += ResourceFieldState(
            x = enemy.position.x,
            y = enemy.position.y,
            inventory = enemy.dropInventory,
            spawnsRevealed = true,
        )
    }
}

private val enemyButtonColor = Color(0xFFDD0000)

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
            position = WindowPosition(state.position.x.dp, state.position.y.dp),
            width = state.position.width.dp,
            height = state.position.height.dp,
        ),
        resizable = false,
        title = "Enemy $health",
        alwaysOnTop = true,
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

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(health)
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = enemyButtonColor, contentColor = Color.White),
            ) {
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
        enemy.position = enemy.position.translated(enemy.velocity)
    }
}


fun followPlayer(player: PlayerState, enemies: SnapshotStateList<EnemyState>) {
    enemies.forEach { enemy ->
        enemy.velocity += (player.position.midOffset - enemy.position.midOffset)
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
        if (me != other && me.position.midOffset.distance(other.position.midOffset) < 150) {
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
        val meMid = me.position.midOffset
        val otherMid = other.position.midOffset
        if (me != other && meMid.distance(otherMid) < 150) {
            v += otherMid - meMid
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
        if (me != other && me.position.midOffset.distance(other.position.midOffset) < 150) {
            v += other.position.midOffset
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
