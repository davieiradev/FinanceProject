
package br.edu.ifal.financeproject.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Locale
import br.edu.ifal.financeproject.domain.CarteiraCripto
import br.edu.ifal.financeproject.viewmodel.CarteiraCriptoViewModel

@Composable
fun TelaCarteiraCripto(
    viewModel: CarteiraCriptoViewModel,
    modifier: Modifier = Modifier
) {
    val carteira by viewModel.carteira.collectAsState()

    val totalInvestido = carteira.sumOf {
        it.quantidade * it.precoMedioCompra
    }

    val valorAtual = carteira.sumOf {
        it.quantidade * it.cotacaoReferencia
    }

    val resultado = valorAtual - totalInvestido
    val percentual = if (totalInvestido > 0) {
        (resultado / totalInvestido) * 100
    } else {
        0.0
    }

    val positivo = resultado >= 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Wallet,
                contentDescription = "Carteira"
            )

            Text(
                text = "Minha carteira",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Patrimônio de referência",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "${formatarValor(valorAtual)} USDT",
                    style = MaterialTheme.typography.headlineMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (positivo) {
                            Icons.Filled.TrendingUp
                        } else {
                            Icons.Filled.TrendingDown
                        },
                        contentDescription = if (positivo) "Ganho" else "Perda",
                        tint = if (positivo) {
                            Color(0xFF2E7D32)
                        } else {
                            Color(0xFFC62828)
                        }
                    )

                    Text(
                        text = "${if (positivo) "+" else ""}${formatarValor(resultado)} USDT (${formatarValor(percentual)}%)",
                        color = if (positivo) {
                            Color(0xFF2E7D32)
                        } else {
                            Color(0xFFC62828)
                        }
                    )
                }

                Text(
                    text = "Total investido: ${formatarValor(totalInvestido)} USDT"
                )
            }
        }

        Text(
            text = "Minhas criptomoedas",
            style = MaterialTheme.typography.titleMedium
        )

        if (carteira.isEmpty()) {
            Text(
                text = "Nenhuma criptomoeda encontrada na carteira.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        carteira.forEach { moeda ->
            CartaoCripto(moeda)
        }

        Text(
            text = "Valores demonstrativos armazenados no banco local.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CartaoCripto(moeda: CarteiraCripto) {
    val totalInvestido = moeda.quantidade * moeda.precoMedioCompra
    val valorAtual = moeda.quantidade * moeda.cotacaoReferencia
    val resultado = valorAtual - totalInvestido

    val percentual = if (totalInvestido > 0) {
        (resultado / totalInvestido) * 100
    } else {
        0.0
    }

    val positivo = resultado >= 0

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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (positivo) {
                        Icons.Filled.TrendingUp
                    } else {
                        Icons.Filled.TrendingDown
                    },
                    contentDescription = if (positivo) "Ganho" else "Perda",
                    tint = if (positivo) {
                        Color(0xFF2E7D32)
                    } else {
                        Color(0xFFC62828)
                    }
                )

                Text(
                    text = "${if (positivo) "+" else ""}${formatarValor(resultado)} USDT (${formatarValor(percentual)}%)",
                    color = if (positivo) {
                        Color(0xFF2E7D32)
                    } else {
                        Color(0xFFC62828)
                    }
                )
            }
        }
    }
}

private fun formatarValor(valor: Double): String {
    return String.format(Locale.US, "%.2f", valor)
}
