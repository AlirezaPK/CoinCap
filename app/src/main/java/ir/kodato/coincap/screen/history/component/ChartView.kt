package ir.kodato.coincap.screen.history.component

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.anastr.speedometer.SpeedView
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import ir.kodato.coincap.R
import ir.kodato.coincap.screen.history.CandleChartData
import ir.kodato.coincap.screen.history.LineChartData
import ir.kodato.coincap.util.ChartType
import ir.kodato.coincap.util.calculateRsi

@Composable
fun ChartView(
    chartType: ChartType,
    candleChartData: CandleChartData?,
    lineChartData: LineChartData?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    axisTextColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AndroidView(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
                .background(backgroundColor),
            factory = { context ->
                val chart = when (chartType) {
                    is ChartType.Candle -> CandleStickChart(context)
                    is ChartType.Line -> LineChart(context)
                }

                val candleEntries = mutableListOf<CandleEntry>()
                candleChartData?.let { candleData ->
                    candleEntries.addAll(
                        candleData.candleDataList.mapIndexed { index, data ->
                            CandleEntry(
                                index.toFloat(),
                                data.high,
                                data.low,
                                data.open,
                                data.close
                            )
                        }
                    )
                }
                val lineEntries = mutableListOf<Entry>()
                lineChartData?.let { lineData ->
                    lineEntries.addAll(
                        lineData.x.zip(lineData.y.withIndex()) { x, (index, y) ->
                            Entry(index.toFloat(), y).apply { data = x }
                        }
                    )
                }

                val candleDataSet = CandleDataSet(candleEntries, "dataLabel").apply {
                    color = android.graphics.Color.RED
                    shadowColor = android.graphics.Color.GRAY
                    shadowWidth = 0.7f
                    decreasingColor = android.graphics.Color.RED
                    decreasingPaintStyle = Paint.Style.FILL
                    increasingColor = android.graphics.Color.GREEN
                    increasingPaintStyle = Paint.Style.FILL
                    neutralColor = android.graphics.Color.BLUE
                    setValueTextColor(android.graphics.Color.RED)
                    setDrawValues(false)
                }

                val lineDataSet = LineDataSet(lineEntries, "dataLabel").apply {
                    color = lineColor.toArgb()
                    setDrawValues(false)
                    setDrawCircles(false)
                    setDrawFilled(true)
                }

                when (chartType) {
                    is ChartType.Candle -> chart.data = CandleData(candleDataSet)
                    is ChartType.Line -> chart.data = LineData(lineDataSet)
                }


                when (chartType) {
                    is ChartType.Candle -> {
                        candleChartData?.let {
                            val marker = CustomMarkerView(
                                context,
                                R.layout.custom_marker_view,
                                it.candleDataList.map { data -> data.close },
                                it.startTimeList
                            )
                            chart.marker = marker
                        }
                    }

                    is ChartType.Line ->
                        lineChartData?.let {
                            chart.marker = CustomMarkerView(
                                context,
                                R.layout.custom_marker_view,
                                it.y,
                                it.x
                            )
                        }
                }

                chart.setTouchEnabled(true)
                chart.isDragEnabled = true
                chart.isScaleXEnabled = true
                chart.isScaleYEnabled = false

                chart.description.isEnabled = false
                chart.legend.isEnabled = true

                chart.axisLeft.textColor = axisTextColor.toArgb()
                chart.axisRight.isEnabled = false
                chart.xAxis.textColor = axisTextColor.toArgb()
                chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                chart.xAxis.labelCount = 4
                chart.legend.textColor = axisTextColor.toArgb()

                chart.xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val entry = lineEntries.firstOrNull { it.x == value }
                        return entry?.data as? String ?: ""
                    }
                }

                chart.invalidate()
                chart
            }
        )

        lineChartData?.let {
            val rsi = calculateRsi(lineChartData.y).last()
            SpeedView(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.5f),
                speed = rsi,
                speedText = {
                    Text(
                        "RSI: $rsi",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                unit = ""
            )
        }
    }
}