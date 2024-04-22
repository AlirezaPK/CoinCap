package ir.kodato.coincap.screen.history

import androidx.lifecycle.SavedStateHandle
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
class HistoryViewModel @Inject constructor(
    private val repository: CoinRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id = savedStateHandle.get<String>("id") ?: ""

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    init {
        getHistory(id)
    }

    fun getHistory(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            repository.getCoinHistory(id).collect { result ->
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
                                history = it,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}