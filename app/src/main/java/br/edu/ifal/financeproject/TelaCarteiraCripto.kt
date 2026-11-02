package br.edu.ifal.financeproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.CarteiraCripto
import java.util.Locale

@Composable
fun TelaCarteiraCripto(
    viewModel: CarteiraCriptoViewModel
) {
    val carteira by viewModel.carteira.collectAsState()

    val totalInvestido = carteira.sumOf {
        it.quantidade * it.precoMedioCompra
    }

    val valorAtual = carteira.sumOf {
        it.quantidade * it.cotacaoReferencia
    }

    val resultado = valorAtual - totalInvestido

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Minha carteira",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Total investido: ${formatarValor(totalInvestido)} USDT")
                Text("Valor de referência: ${formatarValor(valorAtual)} USDT")
                Text("Resultado: ${formatarValor(resultado)} USDT")
            }
        }

        Text(
            text = "Criptomoedas",
            style = MaterialTheme.typography.titleMedium
        )

        carteira.forEach { moeda ->
            CartaoCripto(moeda)
        }

        Text(
            text = "Valores demonstrativos armazenados no banco local.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun CartaoCripto(moeda: CarteiraCripto) {
    val totalInvestido = moeda.quantidade * moeda.precoMedioCompra
    val valorAtual = moeda.quantidade * moeda.cotacaoReferencia
    val resultado = valorAtual - totalInvestido

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${moeda.nome} (${moeda.simbolo})",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Quantidade: ${moeda.quantidade}")

            Text(
                "Preço médio: ${formatarValor(moeda.precoMedioCompra)} USDT"
            )

            Text(
                "Cotação de referência: ${formatarValor(moeda.cotacaoReferencia)} USDT"
            )

            Text(
                "Valor investido: ${formatarValor(totalInvestido)} USDT"
            )

            Text(
                "Valor atual: ${formatarValor(valorAtual)} USDT"
            )

            Text(
                "Resultado: ${formatarValor(resultado)} USDT"
            )
        }
    }
}

private fun formatarValor(valor: Double): String {
    return String.format(Locale.US, "%.2f", valor)
}