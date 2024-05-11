package ir.kodato.coincap.screen.history.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GaugeChart(
    modifier: Modifier = Modifier,
    canvasSize: Dp = 180.dp,
    value: Float,
    valueTextColor: Color = MaterialTheme.colorScheme.onSurface,
    mainPercentTextColor: Color = MaterialTheme.colorScheme.onSurface,
    innerLargeLineColor: Color = MaterialTheme.colorScheme.primary,
    innerSmallLineColor: Color = MaterialTheme.colorScheme.outline,
    outerLineColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    filledOuterLineColor: Color = MaterialTheme.colorScheme.primary
) {
    val textMeasurer = rememberTextMeasurer()

    var animatedValue by remember { mutableFloatStateOf(0f) }

    val animatedSweepAngle by animateFloatAsState(
        targetValue = (2.4 * animatedValue).toFloat(),
        animationSpec = tween(1500),
        label = "value animation"
    )

    LaunchedEffect(value) {
        animatedValue = value
    }

    Canvas(
        modifier = modifier
            .size(canvasSize)
            .padding(16.dp)
    ) {

        drawArc(
            size = Size(canvasSize.toPx(), canvasSize.toPx()),
            color = outerLineColor.copy(),
            startAngle = 150f,
            sweepAngle = 240f,
            useCenter = false,
            style = Stroke(
                width = 16.dp.toPx(),
                cap = StrokeCap.Round
            ),
            topLeft = Offset(
                (size.width - canvasSize.toPx()) / 2,
                (size.height - canvasSize.toPx()) / 2
            )
        )

        drawArc(
            size = Size(canvasSize.toPx(), canvasSize.toPx()),
            color = filledOuterLineColor,
            startAngle = 150f,
            sweepAngle = animatedSweepAngle,
            useCenter = false,
            style = Stroke(
                width = 16.dp.toPx(),
                cap = StrokeCap.Round
            ),
            topLeft = Offset(
                (size.width - canvasSize.toPx()) / 2,
                (size.height - canvasSize.toPx()) / 2
            )
        )

        for (percent in 0..100) {
            val angle = 300f - (percent * 2.4f)

            drawLine(
                color = innerSmallLineColor,
                start = pointOnCircle(
                    thetaInDegrees = angle.toDouble(),
                    radius = size.height / 2 + 2.dp.toPx(),
                    cX = center.x,
                    cY = center.y
                ),
                end = pointOnCircle(
                    thetaInDegrees = angle.toDouble(),
                    radius = size.height / 2 - 8.dp.toPx(),
                    cX = center.x,
                    cY = center.y
                ),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )

            if (percent % 10 == 0) {
                drawLine(
                    color = innerLargeLineColor,
                    start = pointOnCircle(
                        thetaInDegrees = angle.toDouble(),
                        radius = size.height / 2 + 2.dp.toPx(),
                        cX = center.x,
                        cY = center.y
                    ),
                    end = pointOnCircle(
                        thetaInDegrees = angle.toDouble(),
                        radius = size.height / 2 - 12.dp.toPx(),
                        cX = center.x,
                        cY = center.y
                    ),
                    strokeWidth = 1.dp.toPx(),
                    cap = StrokeCap.Round
                )

                val percentText = textMeasurer.measure(
                    percent.toString(),
                    style = TextStyle(
                        fontSize = 8.sp,
                        color = mainPercentTextColor
                    )
                )
                val textOffset = pointOnCircle(
                    thetaInDegrees = angle.toDouble(),
                    radius = size.height / 2 - 16.dp.toPx() - percentText.size.width / 2 - 4.dp.toPx(),
                    cX = center.x - percentText.size.width / 2,
                    cY = center.y - percentText.size.height / 2
                )
                drawText(
                    textLayoutResult = percentText,
                    topLeft = textOffset
                )
            }
        }

        val valueLayoutResult =
            textMeasurer.measure(
                text = value.toString(),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueTextColor
                )
            )
        val textSize = valueLayoutResult.size
        drawText(
            textLayoutResult = valueLayoutResult,
            topLeft = Offset(
                x = size.width / 2 - textSize.width / 2f,
                y = size.height / 2 - textSize.height / 2f
            )
        )
    }
}

private fun pointOnCircle(
    thetaInDegrees: Double,
    radius: Float,
    cX: Float = 0f,
    cY: Float = 0f
): Offset {
    val x = cX + (radius * sin(Math.toRadians(thetaInDegrees)).toFloat())
    val y = cY + (radius * cos(Math.toRadians(thetaInDegrees)).toFloat())

    return Offset(x, y)
}

@Preview(showBackground = true)
@Composable
fun SpeedometerPreview() {
    GaugeChart(value = 19f)
}