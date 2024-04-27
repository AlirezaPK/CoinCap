package ir.kodato.coincap.screen.history.component

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
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
import ir.kodato.coincap.util.CoinCandleData

@Composable
fun CandleChart(
    coinCandleDataList: List<CoinCandleData>,
    dataLabel: String,
    modifier: Modifier = Modifier,
    axisTextColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    dataSetColor: Int = android.graphics.Color.RED,
    shadowLineColor: Int = android.graphics.Color.GRAY,
    shadowLineWidth: Float = 0.7f,
    drawValues: Boolean = false,
    decreaseColor: Int = android.graphics.Color.RED,
    decreasePaintStyle: Paint.Style = Paint.Style.FILL,
    increaseColor: Int = android.graphics.Color.rgb(122, 242, 84),
    increasePaintStyle: Paint.Style = Paint.Style.STROKE,
    normalColor: Int = android.graphics.Color.BLUE,
    valueTextColor: Int = android.graphics.Color.RED,
    descriptionEnabled: Boolean = false,
    legendEnabled: Boolean = true,
    xAxisPosition: XAxis.XAxisPosition = XAxis.XAxisPosition.BOTTOM,
    xAxisLabels: List<String>,
) {

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        factory = { context ->
            val chart = CandleStickChart(context)

            val entries = coinCandleDataList.mapIndexed { index, data ->
                CandleEntry(
                    index.toFloat(),
                    data.high,
                    data.low,
                    data.open,
                    data.close
                )
            }

            val dataSet = CandleDataSet(entries, dataLabel).apply {
                color = dataSetColor
                shadowColor = shadowLineColor
                shadowWidth = shadowLineWidth
                decreasingColor = decreaseColor
                decreasingPaintStyle = decreasePaintStyle
                increasingColor = increaseColor
                increasingPaintStyle = increasePaintStyle
                neutralColor = normalColor
                setValueTextColor(valueTextColor)
                setDrawValues(drawValues)
            }

            chart.data = CandleData(dataSet)

            val marker = CustomMarkerView(
                context,
                R.layout.custom_marker_view,
                coinCandleDataList.map { it.close },
                xAxisLabels
            )
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

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

            chart.invalidate()
            chart
        }
    )
}