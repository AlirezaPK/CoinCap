package ir.kodato.coincap.util

fun calculateRsi(
    prices: List<Float>,
    period: Int = 14
): List<Float> {
    val gains = mutableListOf<Float>()
    val losses = mutableListOf<Float>()
    val rsiData = mutableListOf<Float>()

    for (i in 1 until prices.size) {
        val gain = prices[i] - prices[i - 1]
        val loss = prices[i - 1] - prices[i]

        if (gain > 0) {
            gains.add(gain)
            losses.add(0f)
        } else {
            gains.add(0f)
            losses.add(loss)
        }

        if (gains.size >= period) {
            val avgGain = gains.takeLast(period).sum() / period
            val avgLoss = losses.takeLast(period).sum() / period

            if (avgLoss == 0f) {
                rsiData.add(100f)
            } else {
                val rs = avgGain / avgLoss
                val rsi = 100f - (100f / (1f + rs))
                rsiData.add(rsi)
            }
        }
    }

    return rsiData
}

fun calculateRsiAtTime(
    prices: List<Float>,
    time: Int,
    period: Int = 14
): Float? {
    if (time !in 0 until prices.size) {
        return null
    }
    val pricesUpToTime = prices.subList(0, time + 1)
    val rsiUpToTime = calculateRsi(pricesUpToTime, period)
    return rsiUpToTime.lastOrNull()
}