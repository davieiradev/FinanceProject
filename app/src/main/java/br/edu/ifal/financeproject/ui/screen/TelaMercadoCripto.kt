package br.edu.ifal.financeproject.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale
import br.edu.ifal.financeproject.viewmodel.MercadoCriptoViewModel

@Composable
fun TelaMercadoCripto(
    viewModel: MercadoCriptoViewModel,
    modifier: Modifier = Modifier
) {
    val cotacoes by viewModel.cotacoes.collectAsState()
    val carregando by viewModel.carregando.collectAsState()
    val erro by viewModel.erro.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mercado de criptomoedas",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Cotações em USDT",
            style = MaterialTheme.typography.bodyMedium
        )

        if (carregando && cotacoes.isEmpty()) {
            CircularProgressIndicator()
        }

        erro?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        cotacoes.forEach { moeda ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val nome = when (moeda.symbol) {
                        "BTCUSDT" -> "Bitcoin (BTC)"
                        "ETHUSDT" -> "Ethereum (ETH)"
                        "SOLUSDT" -> "Solana (SOL)"
                        else -> moeda.symbol
                    }

                    val preco = moeda.lastPrice.toDoubleOrNull() ?: 0.0
                    val variacao = moeda.priceChangePercent.toDoubleOrNull() ?: 0.0

                    Text(
                        text = nome,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Preço: ${String.format(Locale.US, "%.2f", preco)} USDT"
                    )

                    Text(
                        text = "Variação em 24h: ${String.format(Locale.US, "%.2f", variacao)}%"
                    )
                }
            }
        }

        Text(
            text = "Atualização automática a cada 30 segundos.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}