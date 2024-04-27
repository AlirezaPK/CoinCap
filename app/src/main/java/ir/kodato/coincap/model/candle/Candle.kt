package ir.kodato.coincap.model.candle

data class Candle(
    val code: String,
    val data: List<List<String>>
)