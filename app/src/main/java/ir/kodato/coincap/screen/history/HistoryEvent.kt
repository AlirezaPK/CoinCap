package ir.kodato.coincap.screen.history

import ir.kodato.coincap.util.ChartState
import ir.kodato.coincap.util.Timeframe

sealed interface HistoryEvent {
    data class ChangeTimeframeDialogVisibility(val isVisible: Boolean) : HistoryEvent
    data class ChangeTimeframe(val timeframe: Timeframe) : HistoryEvent
    data object ChangeChartType : HistoryEvent
    data class ChangeChartState(val chartState: ChartState) : HistoryEvent
}