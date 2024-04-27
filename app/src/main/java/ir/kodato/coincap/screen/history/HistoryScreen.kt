package ir.kodato.coincap.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ir.kodato.coincap.screen.history.component.CandleChart
import ir.kodato.coincap.screen.history.component.LineChart
import ir.kodato.coincap.screen.history.component.RsiChart
import ir.kodato.coincap.util.ChartType
import ir.kodato.coincap.util.CoinCandleData
import ir.kodato.coincap.util.Timeframe
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    historyState: HistoryState,
    coinName: String,
    onErrorButtonClick: () -> Unit,
    onEvent: (HistoryEvent) -> Unit,
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
                },
                actions = {
                    IconButton(
                        onClick = {
                            onEvent(HistoryEvent.ChangeChartType)
                        }
                    ) {
                        Icon(
                            if (historyState.selectedChartType == ChartType.Line)
                                Icons.Filled.CandlestickChart
                            else
                                Icons.AutoMirrored.Filled.ShowChart,
                            "Change Chart"
                        )
                    }

                    IconButton(
                        onClick = {
                            onEvent(HistoryEvent.ChangeTimeframeDialogVisibility(true))
                        }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            "Timeframe Dialog"
                        )
                    }

                    DropdownMenu(
                        expanded = historyState.isTimeframeDialogOpen,
                        onDismissRequest = {
                            onEvent(HistoryEvent.ChangeTimeframeDialogVisibility(false))
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "D1") },
                            onClick = {
                                onEvent(HistoryEvent.ChangeTimeframe(Timeframe.D1))
                                onEvent(HistoryEvent.ChangeTimeframeDialogVisibility(false))
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(text = "H6") },
                            onClick = {
                                onEvent(HistoryEvent.ChangeTimeframe(Timeframe.H6))
                                onEvent(HistoryEvent.ChangeTimeframeDialogVisibility(false))
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(text = "H12") },
                            onClick = {
                                onEvent(HistoryEvent.ChangeTimeframe(Timeframe.H12))
                                onEvent(HistoryEvent.ChangeTimeframeDialogVisibility(false))
                            }
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
                val pattern = when (historyState.selectedTimeframe) {
                    Timeframe.D1 -> "MMM dd"
                    Timeframe.H6 -> "MMM dd - HH:mm"
                    Timeframe.H12 -> "MMM dd - HH:mm"
                }
                val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

                when (historyState.selectedChartType) {

                    ChartType.Candle -> {
                        historyState.candle?.let { candle ->
                            val candleDataList = mutableListOf<CoinCandleData>()
                            val startTimeList = mutableListOf<String>()

                            for (i in candle.data) {
                                val coinCandleData = CoinCandleData(
                                    startTime = i[0].toLong(),
                                    open = i[1].toFloat(),
                                    close = i[2].toFloat(),
                                    high = i[3].toFloat(),
                                    low = i[4].toFloat(),
                                    volume = i[5].toFloat(),
                                    transactionAmount = i[6].toFloat()
                                )
                                candleDataList.add(coinCandleData)
                                startTimeList.add(dateFormat.format(coinCandleData.startTime * 1000))
                            }

                            CandleChart(
                                candleDataList.reversed(),
                                dataLabel = "DataLabel",
                                xAxisLabels = startTimeList.reversed()
                            )
                        }
                    }

                    ChartType.Line -> {
                        historyState.history?.let { history ->
                            val x = mutableListOf<String>()
                            val y = mutableListOf<Float>()

                            for (i in history.data) {
                                x.add(dateFormat.format(i.time))
                                y.add(i.priceUsd.toFloat())
                            }

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                LineChart(
                                    modifier = Modifier.weight(1f),
                                    xData = x,
                                    yData = y,
                                    dataLabel = "DataLabel",
                                    chartState = historyState.chartState,
                                    onEvent = onEvent
                                )

                                RsiChart(
                                    modifier = Modifier.weight(0.5f),
                                    rsiData = y,
                                    xData = x,
                                    chartState = historyState.chartState
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}