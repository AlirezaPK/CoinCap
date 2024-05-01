package ir.kodato.coincap.screen.history.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ir.kodato.coincap.screen.history.HistoryEvent
import ir.kodato.coincap.util.ChartType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenTopAppBar(
    navController:NavHostController,
    coinName:String,
    selectedChartType: ChartType,
    onEvent: (HistoryEvent) -> Unit
) {
    TopAppBar(
        title = {
            Text(text = coinName)
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    onEvent(HistoryEvent.ChangeChartType)
                }
            ) {
                Icon(
                    if (selectedChartType == ChartType.Line)
                        Icons.Filled.CandlestickChart
                    else
                        Icons.AutoMirrored.Filled.ShowChart,
                    "Change Chart"
                )
            }
        }
    )
}