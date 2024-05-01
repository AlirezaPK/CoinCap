package ir.kodato.coincap.screen.history

import ir.kodato.coincap.model.candle.Candle
import ir.kodato.coincap.model.history.History
import ir.kodato.coincap.util.ChartType
import ir.kodato.coincap.util.CoinCandleData
import ir.kodato.coincap.util.Timeframe

data class HistoryState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val history: History? = null,
    val candle: Candle? = null,
    val selectedTimeframe: Timeframe = Timeframe.D1,
    val selectedChartType: ChartType = ChartType.Line,
    val candleChartData: CandleChartData? = null,
    val lineChartData: LineChartData? = null,
)

data class CandleChartData(
    val candleDataList: List<CoinCandleData>,
    val startTimeList: List<String>
)

data class LineChartData(
    val x: List<String>,
    val y: List<Float>
)
