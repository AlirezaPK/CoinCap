package ir.kodato.coincap.api

import ir.kodato.coincap.model.coin.Coin
import retrofit2.http.GET

interface CoinCapApi {

    @GET("assets")
    suspend fun getCoins(): Coin
}