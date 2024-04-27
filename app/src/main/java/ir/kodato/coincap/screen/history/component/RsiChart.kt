package ir.kodato.coincap.screen.history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import ir.kodato.coincap.util.ChartState
import ir.kodato.coincap.util.calculateRsi

@Composable
fun RsiChart(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    rsiData: List<Float>,
    xData: List<String>,
    rsiLineColor: Color = MaterialTheme.colorScheme.tertiary,
    drawValues: Boolean = false,
    drawCircles: Boolean = false,
    rsiLineWidth: Float = 2f,
    chartState: ChartState,
) {

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        factory = { context ->
            val chart = LineChart(context)

            chart.scaleX = chartState.scaleX
            chart.scaleY = chartState.scaleY

            val rsiValues = calculateRsi(rsiData)

            val rsiEntries = xData.zip(rsiValues.withIndex()) { _, (index, rsi) ->
                Entry(index.toFloat(), rsi)
            }

            val rsiDataSet = LineDataSet(rsiEntries, "RSI").apply {
                color = rsiLineColor.toArgb()
                setDrawValues(drawValues)
                setDrawCircles(drawCircles)
                lineWidth = rsiLineWidth
            }

            val lineData = LineData(rsiDataSet)
            chart.data = lineData

            chart.invalidate()
            chart
        }
    )
}