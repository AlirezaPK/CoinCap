package ir.kodato.coincap.screen.history.component

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
import androidx.compose.ui.unit.dp
import com.github.anastr.speedometer.SpeedView
import ir.kodato.coincap.screen.history.CandleChartData
import ir.kodato.coincap.util.ChartType
import ir.kodato.coincap.util.calculateRsi

@Composable
fun ChartView(
    chartType: ChartType,
    candleChartData: CandleChartData?,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (chartType) {
            ChartType.Candle -> {
                CandleChartComp(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(backgroundColor),
                    candleChartData = candleChartData
                )
            }

            ChartType.Line -> {
                LineChartComp(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(backgroundColor),
                    candleChartData = candleChartData
                )
            }
        }

        candleChartData?.let {
            val rsi = calculateRsi(candleChartData.candleDataList.map { it.close }).last()
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