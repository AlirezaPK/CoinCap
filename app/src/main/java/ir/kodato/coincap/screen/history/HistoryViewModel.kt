package ir.kodato.coincap.screen.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.kodato.coincap.repository.CoinRepository
import ir.kodato.coincap.util.ChartType
import ir.kodato.coincap.util.Resource
import ir.kodato.coincap.util.Timeframe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
            is HistoryEvent.ChangeTimeframeDialogVisibility -> {
                _state.value = _state.value.copy(isTimeframeDialogOpen = event.isVisible)
            }

            is HistoryEvent.ChangeTimeframe -> {
                getHistory(id, event.timeframe)
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

            is HistoryEvent.ChangeChartState -> {
                _state.value = _state.value.copy(chartState = event.chartState)
            }
        }
    }

    private fun getHistory(
        id: String,
        timeframe: Timeframe = _state.value.selectedTimeframe,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

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
                        result.data?.let {
                            _state.value = _state.value.copy(
                                history = it,
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
            _state.value = _state.value.copy(isLoading = true)

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
                        result.data?.let {
                            _state.value = _state.value.copy(
                                candle = it,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}