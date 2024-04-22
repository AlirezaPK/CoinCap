package ir.kodato.coincap.screen.history

import ir.kodato.coincap.model.history.History

data class HistoryState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val history: History? = null
)
