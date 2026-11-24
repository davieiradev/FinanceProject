package br.edu.ifal.financeproject.ui.telas

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.dados.modelo.GastoComParcelas
import br.edu.ifal.financeproject.financas.alertasRecorrencia
import br.edu.ifal.financeproject.financas.formatarMoeda
import br.edu.ifal.financeproject.notificacoes.notificacoesPermitidas
import br.edu.ifal.financeproject.ui.componentes.CampoDinheiro
import br.edu.ifal.financeproject.ui.componentes.SimuladorJurosDialog
import br.edu.ifal.financeproject.ui.theme.EstiloValor
import br.edu.ifal.financeproject.ui.theme.EstiloValorGrande
import br.edu.ifal.financeproject.ui.theme.FluxoCard
import br.edu.ifal.financeproject.ui.theme.LogoFluxo

@Composable
fun TelaResumo(
    registros: List<GastoComParcelas>,
    limite: Double?,
    notificacoesPermitidas: Boolean,
    onSolicitarPermissaoDeNotificacao: () -> Unit,
    onSalvarLimite: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var editarLimite by rememberSaveable { mutableStateOf(false) }
    var valorLimite by rememberSaveable { mutableStateOf("") }
    var erroLimite by rememberSaveable { mutableStateOf<String?>(null) }
    var simulador by rememberSaveable { mutableStateOf(false) }
    if (simulador) SimuladorJurosDialog { simulador = false }

    val gastos = registros.map { it.gasto }

    if (editarLimite) {
        AlertDialog(
            onDismissRequest = { editarLimite = false },
            title = { Text("Limite de gastos") },
            text = {
                Column {
                    Text("Defina o valor máximo para acompanhar os gastos cadastrados.")
                    CampoDinheiro("Limite", valorLimite) { valorLimite = it; erroLimite = null }
                    erroLimite?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val valor = valorLimite.toLongOrNull()?.takeIf { it > 0 }?.div(100.0)
                    if (valor == null) {
                        erroLimite = "Informe um valor maior que zero, com até duas casas decimais."
                    } else {
                        onSalvarLimite(valor)
                        editarLimite = false
                    }
                }) { Text("Salvar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { editarLimite = false }) { Text("Cancelar") }
            }
        )
    }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LogoFluxo(altura = 48.dp)
            Spacer(Modifier.width(12.dp))
            Text("Fluxo Finance", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(20.dp))

        FluxoCard(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Simulador de juros", style = MaterialTheme.typography.titleLarge)
                Text("Calcule os juros e o valor final de uma compra a prazo.")
                OutlinedButton(onClick = { simulador = true }) { Text("SIMULAR") }
            }
        }
        Spacer(Modifier.height(16.dp))
        val alertas = alertasRecorrencia(gastos)
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
                        Text("Gastos recorrentes", style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f).semantics { heading() })
                    }
                    Text("Algumas categorias aparecem com frequência. Que tal revisar com calma?")
                    alertas.forEach { alerta ->
                        Text("${alerta.categoria} • ${alerta.quantidade} registros",
                            style = MaterialTheme.typography.titleMedium)
                        Text(formatarMoeda(alerta.total), style = EstiloValorGrande)
                    }
                    Text("Aviso a partir de 3 gastos em Lazer ou em Outros nos últimos 7 dias.",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        FluxoCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Visão geral", style = MaterialTheme.typography.titleLarge)
                Text("Total lançado: ${formatarMoeda(gastos.sumOf { it.valor })}", style = EstiloValor)
                Text("O saldo diminui ao lançar um gasto à vista ou pagar uma parcela.")
                if (!notificacoesPermitidas) {
                    OutlinedButton(onClick = onSolicitarPermissaoDeNotificacao) { Text("Permitir alertas") }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        FluxoCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                val limiteMensal = limite ?: 0.0
                val totalGasto = registros.sumOf { it.pagoCentavos } / 100.0
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
                    if (limite == null) "Defina seu limite" else formatarMoeda(saldoDisponivel),
                    style = EstiloValorGrande,
                    color = corDoSaldo
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text("Total pago: ${formatarMoeda(totalGasto)}", style = MaterialTheme.typography.bodySmall)
                    Text(if (limite == null) "Limite não definido" else "Limite: ${formatarMoeda(limiteMensal)}", style = MaterialTheme.typography.bodySmall)
                }

                Button(onClick = {
                    valorLimite = limite?.let { java.math.BigDecimal.valueOf(it).movePointRight(2).toBigInteger().toString() } ?: ""
                    erroLimite = null
                    editarLimite = true
                }) { Text(if (limite == null) "Definir limite" else "Alterar limite") }

                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = {progressoLimpo},
                    modifier = Modifier.fillMaxWidth().height(12.dp),
                    color = if (percentagemGasta > 0.9f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    if (gastos.isEmpty()) "Que tal registrar seu primeiro gasto?"
                    else "Você já registrou ${gastos.size} gasto(s). Bom trabalho!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Últimos lançamentos", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        if (gastos.isEmpty()) {
            Text(
                "Nada por aqui ainda. Seus gastos aparecerão assim que você lançar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            gastos.take(3).forEach { gasto ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(gasto.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(
                            gasto.data,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(formatarMoeda(gasto.valor), style = EstiloValor)
                }
            }
        }
    }
}
