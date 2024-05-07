package ir.kodato.coincap.screen.history.component

import android.graphics.Paint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import ir.kodato.coincap.R
import ir.kodato.coincap.screen.history.CandleChartData

@Composable
fun CandleChartComp(
    modifier: Modifier = Modifier,
    candleChartData: CandleChartData?,
    axisTextColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    candleChartData?.let { candleData ->
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val chart = CandleStickChart(context)

                val candleEntries = mutableListOf<CandleEntry>()
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
                chart.data = CandleData(candleDataSet)

                val marker = CustomMarkerView(
                    context,
                    R.layout.custom_marker_view,
                    candleData.candleDataList.map { data -> data.close },
                    candleData.startTimeList
                )
                chart.marker = marker

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
                chart.xAxis.labelCount = 3
                chart.legend.textColor = axisTextColor.toArgb()

                chart.xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index in candleChartData.startTimeList.indices) {
                            candleChartData.startTimeList[index]
                        } else {
                            ""
                        }
                    }
                }

                chart.invalidate()
                chart
            }
        )
    }
}