package br.edu.ifal.financeproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.model.Despesa
import br.edu.ifal.financeproject.model.Divida
import br.edu.ifal.financeproject.util.alertasRecorrencia
import br.edu.ifal.financeproject.util.calcularTotais
import br.edu.ifal.financeproject.util.formatarMoeda
import br.edu.ifal.financeproject.ui.theme.EstiloValor
import br.edu.ifal.financeproject.ui.theme.EstiloValorGrande
import br.edu.ifal.financeproject.ui.theme.FluxoCard
import br.edu.ifal.financeproject.ui.theme.LogoFluxo

@Composable
fun TelaResumo(
    despesas: List<Despesa>,
    dividas: List<Divida>,
    limite: Double?,
    onSalvarLimite: (Double) -> Unit, // Mantido para não quebrar a MainActivity
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LogoFluxo(altura = 48.dp)
            Spacer(Modifier.width(12.dp))
            Text("Fluxo Finance", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(20.dp))

        val alertas = alertasRecorrencia(despesas, dividas = dividas)
        if (alertas.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Warning, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Atenção aos gastos e dívidas", style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f).semantics { heading() })
                    }
                    Text("Algumas categorias aparecem com frequência. Que tal revisar com calma?")
                    alertas.forEach { alerta ->
                        Text("${alerta.categoria}\n${alerta.quantidade} registros",
                            style = MaterialTheme.typography.titleMedium)
                        Text(formatarMoeda(alerta.total), style = EstiloValorGrande)
                        Text("Gastos nos últimos 7 dias: ${alerta.quantidadeDespesas}\n${formatarMoeda(alerta.totalDespesas)}")
                        Text("Dívidas em aberto: ${alerta.quantidadeDividas}\n${formatarMoeda(alerta.totalDividas)}")
                    }
                    Text("Aviso a partir de 3 registros na mesma categoria (Lazer ou Outros), somando gastos dos últimos 7 dias e todas as dívidas não pagas, inclusive com vencimento futuro.",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        val totais = calcularTotais(despesas, dividas)
        FluxoCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Visão geral", style = MaterialTheme.typography.titleLarge)
                Text("Gastos registrados: ${formatarMoeda(totais.gastos)}")
                Text("Dívidas cadastradas: ${formatarMoeda(totais.dividas)}")
                Text("Total acumulado: ${formatarMoeda(totais.acumulado)}", style = EstiloValor)
                Text("Todos os registros. Dívidas são somadas separadamente e não descontam o limite de gastos.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(16.dp))

        FluxoCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                val limiteMensal = limite ?: 0.0
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
                    if (limite == null) "Sem limite definido" else formatarMoeda(saldoDisponivel),
                    style = EstiloValorGrande,
                    color = corDoSaldo
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text("Total gasto : ${formatarMoeda(totalGasto)}", style = MaterialTheme.typography.bodySmall)
                    Text(if (limite == null) "Limite não definido" else "Limite: ${formatarMoeda(limiteMensal)}", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = {progressoLimpo},
                    modifier = Modifier.fillMaxWidth().height(12.dp),
                    color = if (percentagemGasta > 0.9f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    if (despesas.isEmpty()) "Nenhum gasto registrado ainda."
                    else "Você já registrou ${despesas.size} despesa(s).",
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