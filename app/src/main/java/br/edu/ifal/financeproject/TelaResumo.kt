package br.edu.ifal.financeproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.ui.theme.EstiloValor
import br.edu.ifal.financeproject.ui.theme.EstiloValorGrande
import br.edu.ifal.financeproject.ui.theme.FluxoCard
import br.edu.ifal.financeproject.ui.theme.LogoFluxo

@Composable
fun TelaResumo(
    despesas: List<Despesa>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LogoFluxo(altura = 48.dp)
            Spacer(Modifier.width(12.dp))
            Text("Fluxo Finance", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(20.dp))

        FluxoCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                val limiteMensal = 2000.00
                val totalGasto = despesas.sumOf{it.valor}
                val saldoDisponivel = limiteMensal - totalGasto

                val percentagemGasta = if (limiteMensal > 0) (totalGasto / limiteMensal).toFloat() else 0f
                val progressoLimpo = percentagemGasta.coerceIn(0f, 1f)

                Text(
                    "Saldo Disponível",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val corDoSaldo = if (saldoDisponivel >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                Text(
                    formatarMoeda(saldoDisponivel),
                    style = EstiloValorGrande,
                    color = corDoSaldo
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text("Total gasto : ${formatarMoeda(totalGasto)}", style = MaterialTheme.typography.bodySmall)
                    Text("Limite: ${formatarMoeda(limiteMensal)}", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = {progressoLimpo},
                    modifier = Modifier.fillMaxWidth().height(12.dp),
                    color = if (percentagemGasta > 0.9f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                Spacer(Modifier.height(12.dp))

                Text(
                    if (despesas.isEmpty()) "Que tal registrar o seu primeiro gasto?"
                    else "Você já registrou ${despesas.size} despesa(s). Bom trabalho!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Últimos lançamentos", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        if (despesas.isEmpty()) {
            Text(
                "Nada por aqui ainda. Seus gastos aparecerão assim que você lançar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            despesas.take(3).forEach { despesa ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(despesa.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(
                            despesa.data,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(formatarMoeda(despesa.valor), style = EstiloValor)
                }
            }
        }
    }
}