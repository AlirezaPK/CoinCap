package ir.kodato.coincap.screen.history

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.kodato.coincap.repository.CoinRepository
import ir.kodato.coincap.util.ChartType
import ir.kodato.coincap.util.CoinCandleData
import ir.kodato.coincap.util.Resource
import ir.kodato.coincap.util.Timeframe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CoinRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val id = savedStateHandle.get<String>("id") ?: ""
    private val symbol = savedStateHandle.get<String>("symbol") ?: ""

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    init {
        getHistory(id)
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.ChangeTimeframe -> {
                when (_state.value.selectedChartType) {
                    is ChartType.Line -> getHistory(id, event.timeframe)
                    is ChartType.Candle -> getCandle(symbol, event.timeframe)
                }
                _state.value = _state.value.copy(selectedTimeframe = event.timeframe)
            }

            is HistoryEvent.ChangeChartType -> {
                when (_state.value.selectedChartType) {
                    ChartType.Candle -> {
                        _state.value = _state.value.copy(selectedChartType = ChartType.Line)
                        getHistory(id, _state.value.selectedTimeframe)
                    }

                    ChartType.Line -> {
                        _state.value = _state.value.copy(selectedChartType = ChartType.Candle)
                        getCandle(symbol, _state.value.selectedTimeframe)
                    }
                }
            }
        }
    }

    private fun getHistory(
        id: String,
        timeframe: Timeframe = _state.value.selectedTimeframe,
    ) {
        viewModelScope.launch {

            repository.getCoinHistory(id, timeframe.timeframe).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            errorMessage = result.message ?: "Error",
                            isLoading = false
                        )
                    }

                    is Resource.Success -> {
                        result.data?.let { history ->
                            val x = mutableListOf<String>()
                            val y = mutableListOf<Float>()

                            for (dataPoint in history.data) {
                                x.add(formatTime(timeframe, dataPoint.time))
                                y.add(dataPoint.priceUsd.toFloat())
                            }

                            val lineChartData = LineChartData(x, y)

                            _state.value = _state.value.copy(
                                history = history,
                                lineChartData = lineChartData,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getCandle(
        symbol: String,
        timeframe: Timeframe = _state.value.selectedTimeframe,
    ) {
        viewModelScope.launch {

            repository.getCandle("$symbol-USDT", timeframe.alternateTimeframe).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            errorMessage = result.message ?: "Error",
                            isLoading = false
                        )
                    }

                    is Resource.Success -> {
                        result.data?.let { candle ->
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
                                startTimeList.add(formatTime(timeframe, coinCandleData.startTime))
                            }

                            val candleChartData = CandleChartData(
                                candleDataList.reversed(),
                                startTimeList.reversed()
                            )

                            _state.value = _state.value.copy(
                                candleChartData = candleChartData,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(timeframe: Timeframe, timestamp: Long): String {
    val dateFormat = when (timeframe) {
        Timeframe.D1 -> SimpleDateFormat("MMM dd", Locale.getDefault())
        Timeframe.H6, Timeframe.H12 -> SimpleDateFormat("MMM dd - HH:mm", Locale.getDefault())
    }
    return dateFormat.format(timestamp * 1000)
}