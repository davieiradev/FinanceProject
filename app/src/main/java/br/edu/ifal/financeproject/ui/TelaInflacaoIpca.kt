package br.edu.ifal.financeproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.InflacaoIpcaViewModel
import br.edu.ifal.financeproject.model.DadoEconomico
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

@Composable
fun TelaInflacaoIpca(
    viewModel: InflacaoIpcaViewModel,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsState()
    val resultados = estado.resultados
    val resultadoMaisRecente = resultados.lastOrNull()

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVoltar) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Text("Inflação (IPCA)", style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f))
            IconButton(onClick = viewModel::atualizar, enabled = !estado.carregando) {
                Icon(Icons.Filled.Refresh, contentDescription = "Atualizar valores")
            }
        }

        Text(
            "Variação mensal dos preços no Brasil, consultada no Banco Central.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (estado.erro != null && resultados.isNotEmpty()) {
            Text(
                "Não foi possível atualizar. Exibindo os últimos dados carregados.",
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (estado.carregando && resultados.isNotEmpty()) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }

        if (estado.carregando && resultados.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Carregando resultados...")
            }
        } else if (estado.erro != null && resultados.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(estado.erro!!, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = viewModel::atualizar) { Text("Tentar novamente") }
            }
        } else if (resultadoMaisRecente != null) {
            val valores = resultados.mapNotNull { it.valor.toDoubleOrNull() }
            val acumulado = (resultados.fold(1.0) { fator, resultado ->
                fator * (1 + (resultado.valor.toDoubleOrNull() ?: 0.0) / 100)
            } - 1) * 100
            val mediaMensal = if (valores.isNotEmpty()) valores.average() else 0.0
            val maiorModulo = (valores.maxOfOrNull { abs(it) } ?: 0.0).coerceAtLeast(0.01)

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Resultado mais recente", style = MaterialTheme.typography.titleMedium)
                            Text(mesEAno(resultadoMaisRecente.data),
                                style = MaterialTheme.typography.bodyMedium)
                            Text("${formatarPercentual(resultadoMaisRecente.valor)}%",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CartaoResumoIpca(
                            titulo = "Acumulado no período",
                            valor = "${formatarPercentual(acumulado.toString())}%",
                            modifier = Modifier.weight(1f)
                        )
                        CartaoResumoIpca(
                            titulo = "Média mensal",
                            valor = "${formatarPercentual(mediaMensal.toString())}%",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Últimos resultados", style = MaterialTheme.typography.titleMedium)
                            resultados.asReversed().forEach { resultado ->
                                LinhaGraficoIpca(resultado, maiorModulo)
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Fonte: Banco Central, série SGS 433.",
                        modifier = Modifier.padding(vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CartaoResumoIpca(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.labelMedium)
            Text(valor, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LinhaGraficoIpca(resultado: DadoEconomico, maiorModulo: Double) {
    val valor = resultado.valor.toDoubleOrNull() ?: 0.0

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(mesEAno(resultado.data), style = MaterialTheme.typography.bodyMedium)
            Text("${formatarPercentual(resultado.valor)}%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold)
        }
        LinearProgressIndicator(
            progress = { (abs(valor) / maiorModulo).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

private fun mesEAno(data: String): String {
    val partes = data.split("/")
    return if (partes.size == 3) "${partes[1]}/${partes[2]}" else data
}

private fun formatarPercentual(valor: String): String {
    val numero = valor.toDoubleOrNull() ?: return valor
    return NumberFormat.getNumberInstance(Locale("pt", "BR")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(numero)
}
