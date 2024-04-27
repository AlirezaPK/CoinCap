package ir.kodato.coincap.util

sealed class Timeframe(
    val timeframe: String,
    val alternateTimeframe: String
) {
    data object D1 : Timeframe("d1", "1day")
    data object H6 : Timeframe("h6", "6hour")
    data object H12 : Timeframe("h12", "12hour")
}
