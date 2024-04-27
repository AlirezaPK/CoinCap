package ir.kodato.coincap.util

sealed class ChartType {
    data object Line : ChartType()
    data object Candle : ChartType()
}
