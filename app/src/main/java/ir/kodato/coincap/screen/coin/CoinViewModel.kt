package ir.kodato.coincap.screen.coin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.kodato.coincap.repository.CoinRepository
import ir.kodato.coincap.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val repository: CoinRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CoinState())
    val state = _state.asStateFlow()

    init {
        getCoin()
    }

    fun getCoin() {
        viewModelScope.launch {

            repository.getCoins().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            errorMessage = result.message ?: "Error",
                            isLoading = false
                        )
                    }

                    is Resource.Success -> {
                        result.data?.let {
                            _state.value = _state.value.copy(
                                coin = it,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}