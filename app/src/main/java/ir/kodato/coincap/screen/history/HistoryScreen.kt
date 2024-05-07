package ir.kodato.coincap.screen.history

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ir.kodato.coincap.screen.history.component.HistoryScreenContent
import ir.kodato.coincap.screen.history.component.HistoryScreenTopAppBar

@Composable
fun HistoryScreen(
    navController: NavHostController,
    historyState: HistoryState,
    coinName: String,
    onErrorButtonClick: () -> Unit,
    onEvent: (HistoryEvent) -> Unit,
) {

    Scaffold(
        topBar = {
            HistoryScreenTopAppBar(
                navController = navController,
                coinName = coinName,
                selectedChartType = historyState.selectedChartType,
                onEvent = onEvent
            )
        }
    ) { paddingValues ->

        HistoryScreenContent(
            paddingValues = paddingValues,
            selectedTimeframe = historyState.selectedTimeframe,
            isStateLoading = historyState.isLoading,
            errorMessage = historyState.errorMessage,
            selectedChartType = historyState.selectedChartType,
            candleChartData = historyState.candleChartData,
            onErrorButtonClick = onErrorButtonClick,
            onEvent = onEvent
        )
    }
}