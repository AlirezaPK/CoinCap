package ir.kodato.coincap.screen.history

import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import ir.kodato.coincap.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    historyState: HistoryState,
    coinName: String,
    onErrorButtonClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = coinName)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (historyState.isLoading) {
                CircularProgressIndicator()
            } else if (historyState.errorMessage != "") {
                Text(
                    text = historyState.errorMessage,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onErrorButtonClick() }
                ) {
                    Text(text = "Try Again")
                }
            } else {
                historyState.history?.let { history ->

                    val x = mutableListOf<String>()
                    val y = mutableListOf<Float>()

                    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

                    for (i in history.data) {
                        val date = Date(i.time)
                        x.add(dateFormat.format(date))
                        y.add(i.priceUsd.toFloat())
                    }

                    LineGraph(
                        xData = x,
                        yData = y,
                        dataLabel = "DataLabel"
                    )
                }
            }
        }
    }
}

@Composable
fun LineGraph(
    xData: List<String>,
    yData: List<Float>,
    dataLabel: String,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    axisTextColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    drawValues: Boolean = false,
    drawMarkers: Boolean = false,
    drawFilled: Boolean = true,
    descriptionEnabled: Boolean = false,
    legendEnabled: Boolean = true,
    xAxisPosition: XAxis.XAxisPosition = XAxis.XAxisPosition.BOTTOM
) {

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        factory = { context ->
            val chart = LineChart(context)

            val entries = xData.zip(yData.withIndex()) { x, (index, y) ->
                Entry(index.toFloat(), y).apply { data = x }
            }

            val dataSet = LineDataSet(entries, dataLabel).apply {
                color = lineColor.toArgb()
                setDrawValues(drawValues)
                setDrawCircles(drawMarkers)
                setDrawFilled(drawFilled)
            }

            chart.data = LineData(dataSet)

            val marker = CustomMarkerView(context, R.layout.custom_marker_view, yData, xData)
            chart.marker = marker

//            chart.setOnChartValueSelectedListener(
//                object : OnChartValueSelectedListener {
//                    override fun onValueSelected(e: Entry?, h: Highlight?) {
//                        val xIndex = e!!.x.toInt()
//                        if (xIndex in xData.indices && xIndex in yData.indices) {
//                            val date = xData[xIndex]
//                            val price = yData[xIndex]
//                            Toast.makeText(context, "$date - $price", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onNothingSelected() {
//
//                    }
//                }
//            )

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
            chart.legend.textColor = axisTextColor.toArgb()

            chart.xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val entry = entries.firstOrNull { it.x == value }
                    return entry?.data as? String ?: ""
                }
            }

            chart.invalidate()
            chart
        }
    )
}

class CustomMarkerView(
    context: Context,
    layout: Int,
    private val y: List<Float>,
    private val x: List<String>,
) : MarkerView(context, layout) {

    private var txtViewData: TextView? = null

    init {
        txtViewData = findViewById(R.id.txtViewData)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        try {
            val xAxis = e?.x?.toInt() ?: 0
            val text = "${y[xAxis]} - ${x[xAxis]}"
            txtViewData?.text = text
        } catch (e: IndexOutOfBoundsException) { }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}