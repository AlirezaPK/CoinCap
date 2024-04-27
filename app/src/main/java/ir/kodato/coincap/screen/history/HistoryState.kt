package ir.kodato.coincap.screen.history

import ir.kodato.coincap.model.candle.Candle
import ir.kodato.coincap.model.history.History
import ir.kodato.coincap.util.ChartState
import ir.kodato.coincap.util.ChartType
import ir.kodato.coincap.util.Timeframe

data class HistoryState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val history: History? = null,
    val candle: Candle? = null,
    val isTimeframeDialogOpen: Boolean = false,
    val selectedTimeframe: Timeframe = Timeframe.D1,
    val selectedChartType: ChartType = ChartType.Line,
    val chartState: ChartState = ChartState(),
)
