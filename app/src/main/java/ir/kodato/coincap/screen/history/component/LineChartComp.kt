package ir.kodato.coincap.screen.history.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import ir.kodato.coincap.R
import ir.kodato.coincap.screen.history.CandleChartData

@Composable
fun LineChartComp(
    modifier: Modifier = Modifier,
    candleChartData: CandleChartData?,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    axisTextColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    candleChartData?.let { candleData ->
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val chart = LineChart(context)

                val lineEntries = mutableListOf<Entry>()
                lineEntries.addAll(
                    candleData.candleDataList.mapIndexed { index, data ->
                        Entry(index.toFloat(), data.close)
                    }
                )

                val lineDataSet = LineDataSet(lineEntries, "dataLabel").apply {
                    color = lineColor.toArgb()
                    setDrawValues(false)
                    setDrawCircles(false)
                    setDrawFilled(true)
                }
                chart.data = LineData(lineDataSet)

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