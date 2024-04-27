package ir.kodato.coincap.util

data class CoinCandleData(
    val startTime: Long,
    val open: Float,
    val close: Float,
    val high: Float,
    val low: Float,
    val volume: Float,
    val transactionAmount: Float
)