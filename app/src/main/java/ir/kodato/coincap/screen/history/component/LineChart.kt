package ir.kodato.coincap.screen.history.component

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
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
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import ir.kodato.coincap.R
import ir.kodato.coincap.screen.history.HistoryEvent
import ir.kodato.coincap.util.ChartState

@Composable
fun LineChart(
    xData: List<String>,
    yData: List<Float>,
    dataLabel: String,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    axisTextColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    drawValues: Boolean = false,
    drawCircles: Boolean = false,
    drawFilled: Boolean = true,
    descriptionEnabled: Boolean = false,
    legendEnabled: Boolean = true,
    xAxisPosition: XAxis.XAxisPosition = XAxis.XAxisPosition.BOTTOM,
    chartState: ChartState,
    onEvent: (HistoryEvent) -> Unit,
) {

    val updatedChartState = rememberUpdatedState(chartState)

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        factory = { context ->

            val chart = LineChart(context)

            chart.scaleX = updatedChartState.value.scaleX
            chart.scaleY = updatedChartState.value.scaleY

            val entries = xData.zip(yData.withIndex()) { x, (index, y) ->
                Entry(index.toFloat(), y).apply { data = x }
            }

            val dataSet = LineDataSet(entries, dataLabel).apply {
                color = lineColor.toArgb()
                setDrawValues(drawValues)
                setDrawCircles(drawCircles)
                setDrawFilled(drawFilled)
            }

            val lineData = LineData(dataSet)
            chart.data = lineData

            val marker = CustomMarkerView(context, R.layout.custom_marker_view, yData, xData)
            chart.marker = marker

            chart.setTouchEnabled(true)
            chart.isDragEnabled = true
            chart.isScaleXEnabled = true
            chart.isScaleYEnabled = false

            chart.description.isEnabled = descriptionEnabled
            chart.legend.isEnabled = legendEnabled

            chart.axisLeft.textColor = axisTextColor.toArgb()
            chart.axisRight.isEnabled = false
            chart.xAxis.textColor = axisTextColor.toArgb()
            chart.xAxis.position = xAxisPosition
            chart.xAxis.labelCount = 4
            chart.legend.textColor = axisTextColor.toArgb()

            chart.xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val entry = entries.firstOrNull { it.x == value }
                    return entry?.data as? String ?: ""
                }
            }

            chart.onChartGestureListener = object : OnChartGestureListener {
                override fun onChartGestureStart(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?,
                ) = Unit

                override fun onChartGestureEnd(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?,
                ) {
                    onEvent(
                        HistoryEvent.ChangeChartState(
                            ChartState(
                                chart.scaleX,
                                chart.scaleY
                            )
                        )
                    )
                }

                override fun onChartLongPressed(me: MotionEvent?) = Unit

                override fun onChartDoubleTapped(me: MotionEvent?) = Unit

                override fun onChartSingleTapped(me: MotionEvent?) = Unit

                override fun onChartFling(
                    me1: MotionEvent?,
                    me2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float,
                ) = Unit

                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) = Unit

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) = Unit
            }

            chart.invalidate()
            chart
        }
    )
}