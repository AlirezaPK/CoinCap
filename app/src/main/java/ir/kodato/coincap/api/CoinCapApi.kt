package ir.kodato.coincap.api

import ir.kodato.coincap.model.coin.Coin
import ir.kodato.coincap.model.history.History
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinCapApi {

    @GET("assets")
    suspend fun getCoins(): Coin

    @GET("assets/{id}/history")
    suspend fun getCoinHistory(
        @Path("id") id: String,
        @Query("interval") interval: String = "d1"
    ): History
}