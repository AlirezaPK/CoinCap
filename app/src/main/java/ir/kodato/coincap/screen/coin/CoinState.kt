package ir.kodato.coincap.screen.coin

import ir.kodato.coincap.model.coin.Coin

data class CoinState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val coin: Coin? = null
)
