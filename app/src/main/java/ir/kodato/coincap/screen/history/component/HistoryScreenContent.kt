package ir.kodato.coincap.screen.history.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.kodato.coincap.screen.history.CandleChartData
import ir.kodato.coincap.screen.history.HistoryEvent
import ir.kodato.coincap.screen.history.LineChartData
import ir.kodato.coincap.util.ChartType
import ir.kodato.coincap.util.Timeframe

@Composable
fun HistoryScreenContent(
    paddingValues: PaddingValues,
    selectedTimeframe: Timeframe,
    isStateLoading: Boolean,
    errorMessage: String,
    selectedChartType: ChartType,
    candleChartData: CandleChartData?,
    lineChartData: LineChartData?,
    onErrorButtonClick: () -> Unit,
    onEvent: (HistoryEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimeframeSelector(selectedTimeframe, onEvent)
        if (isStateLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != "") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onErrorButtonClick() }
                ) {
                    Text(text = "Try Again")
                }
            }
        } else {
            ChartView(
                chartType = selectedChartType,
                candleChartData = candleChartData,
                lineChartData = lineChartData
            )
        }
    }
}