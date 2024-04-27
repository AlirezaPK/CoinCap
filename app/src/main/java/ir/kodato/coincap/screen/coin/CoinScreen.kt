package ir.kodato.coincap.screen.coin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ir.kodato.coincap.NavScreen
import ir.kodato.coincap.util.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinScreen(
    navController: NavHostController,
    coinState: CoinState,
    onErrorButtonClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "CoinCap")
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (coinState.isLoading) {
                CircularProgressIndicator()
            } else if (coinState.errorMessage != "") {
                Text(
                    text = coinState.errorMessage,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onErrorButtonClick() }
                ) {
                    Text(text = "Try Again")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    coinState.coin?.let {
                        items(it.data) { coinData ->
                            Card(
                                modifier = Modifier.height(64.dp),
                                onClick = {
                                    navController.navigate(
                                        NavScreen.HistoryScreen.passParams(
                                            coinData.id,
                                            coinData.symbol
                                        )
                                    )
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.widthIn(min = 32.dp),
                                            text = coinData.rank
                                        )

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(text = coinData.name)

                                            Text(
                                                text = coinData.symbol,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            )
                                        }
                                    }

                                    Row {
                                        Text(text = formatPrice(coinData.priceUsd.toDouble()))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}