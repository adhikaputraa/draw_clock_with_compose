package com.example.clock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.clock.ui.theme.*
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockTheme {
                var currentTimeInMs by remember {
                    mutableStateOf(System.currentTimeMillis())
                }
                LaunchedEffect(key1 = true) {
                    while (true) {
                        delay(200)
                        currentTimeInMs = System.currentTimeMillis()
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Clock(
                        modifier = Modifier.size(500.dp),
                        time = { currentTimeInMs },
                        circleRadius = 500f,
                        outerCircleThickness = 50f
                    )
                }
            }
        }
    }
}

@Composable
fun Clock(
    modifier: Modifier = Modifier,
    time: () -> Long,
    circleRadius: Float,
    outerCircleThickness: Float
) {

    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    Box(modifier = modifier) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            circleCenter = Offset(width / 2, height / 2)
            val date = Date(time())
            val cal = Calendar.getInstance()
            cal.time = date
            val hours = cal.get(Calendar.HOUR_OF_DAY)
            val minutes = cal.get(Calendar.MINUTE)
            val seconds = cal.get(Calendar.SECOND)

            drawCircle(
                style = Stroke(
                    width = outerCircleThickness
                ),
                brush = Brush.linearGradient(
                    listOf(
                        white.copy(0.45f),
                        darkGray.copy(0.35f)
                    )
                ),
                radius = circleRadius + outerCircleThickness / 2f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        white.copy(0.45f),
                        darkGray.copy(0.25f)
                    )
                ),
                radius = circleRadius,
                center = circleCenter
            )

            drawCircle(
                color = gray,
                radius = 15f,
                center = circleCenter
            )

            val littleLineLength = circleRadius * 0.1f
            val largeLineLength = circleRadius * 0.2f

            for (i in 0 until 60) {
                val angleInDegrees = i * 360f / 60
                val angleInRad = angleInDegrees * PI / 180f + PI / 2f
                val lineLength = if (i % 5 == 0) largeLineLength else littleLineLength
                val lineThickness = if (i % 5 == 0) 5f else 2f

                val start = Offset(
                    x = (circleRadius * cos(angleInRad) + circleCenter.x).toFloat(),
                    y = (circleRadius * sin(angleInRad) + circleCenter.y).toFloat()
                )
                val end = Offset(
                    x = (circleRadius * cos(angleInRad) + circleCenter.x).toFloat(),
                    y = (circleRadius * sin(angleInRad) + lineLength + circleCenter.y).toFloat()
                )
                rotate(
                    angleInDegrees + 180,
                    pivot = start
                ) {
                    drawLine(
                        color = gray,
                        start = start,
                        end = end,
                        strokeWidth = lineThickness.dp.toPx()
                    )
                }
            }

            val clockHands = listOf(ClockHand.SECONDS, ClockHand.MINUTES, ClockHand.HOUR)

            clockHands.forEach {
                val angleInDegrees = when (it) {
                    ClockHand.SECONDS -> {
                        seconds * 360f / 60f
                    }
                    ClockHand.MINUTES -> {
                        (minutes+seconds / 60f) * 360f/60
                    }
                    ClockHand.HOUR -> {
                        (((hours % 12) / 12f * 60f) + minutes / 12f) * 360f / 60f
                    }
                }
                val lineLength = when (it) {
                    ClockHand.SECONDS -> {
                        circleRadius * 0.8f
                    }
                    ClockHand.MINUTES -> {
                        circleRadius * 0.7f
                    }
                    ClockHand.HOUR -> {
                        circleRadius * 0.5f
                    }
                }
                val lineThickness = when (it) {
                    ClockHand.SECONDS -> {
                        3f
                    }
                    ClockHand.MINUTES -> {
                        7f
                    }
                    ClockHand.HOUR -> {
                        9f
                    }
                }
                val start = Offset(
                    x = circleCenter.x,
                    y = circleCenter.y
                )
                val end = Offset(
                    x = circleCenter.x,
                    y = circleCenter.y + lineLength
                )
                rotate(
                    angleInDegrees - 180,
                    pivot = start
                ) {
                    drawLine(
                        color = if (it == ClockHand.SECONDS) redOrange else gray,
                        start = start,
                        end = end,
                        strokeWidth = lineThickness.dp.toPx()
                    )
                }
            }
        }
    }
}

enum class ClockHand {
    SECONDS,
    MINUTES,
    HOUR
}