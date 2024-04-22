package ir.kodato.coincap.repository

import ir.kodato.coincap.model.coin.Coin
import ir.kodato.coincap.model.history.History
import ir.kodato.coincap.api.CoinCapApi
import ir.kodato.coincap.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val api: CoinCapApi
) {

    suspend fun getCoins(): Flow<Resource<Coin>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val coins = api.getCoins()
                emit(Resource.Success(coins))

            } catch (e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server. Check your internet connection."))
            }
        }
    }

    suspend fun getCoinHistory(id: String): Flow<Resource<History>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val coinHistory = api.getCoinHistory(id)
                emit(Resource.Success(coinHistory))

            } catch (e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server. Check your internet connection."))
            }
        }
    }
}