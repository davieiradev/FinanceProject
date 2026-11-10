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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import java.util.Locale
import java.util.Calendar
import java.text.SimpleDateFormat
import java.text.ParsePosition
import br.edu.ifal.financeproject.ui.theme.EstiloValor
import br.edu.ifal.financeproject.ui.theme.EstiloValorGrande
import br.edu.ifal.financeproject.ui.theme.FluxoCard
import br.edu.ifal.financeproject.ui.theme.LogoFluxo

@Composable
fun TelaResumo(
    despesas: List<Despesa>,
    dividas: List<Divida>,
    limite: Double?,
    onSalvarLimite: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var editarLimite by rememberSaveable { mutableStateOf(false) }
    var valorLimite by rememberSaveable { mutableStateOf("") }
    var erroLimite by rememberSaveable { mutableStateOf<String?>(null) }
    var periodoSelecionado by rememberSaveable { mutableStateOf("Hoje") }

    if (editarLimite) {
        AlertDialog(
            onDismissRequest = { editarLimite = false },
            title = { Text("Limite de gastos") },
            text = {
                Column {
                    Text("Defina o valor máximo para acompanhar as despesas cadastradas.")
                    OutlinedTextField(
                        value = valorLimite,
                        onValueChange = { valorLimite = it; erroLimite = null },
                        label = { Text("Limite (R$)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = erroLimite != null,
                        supportingText = { erroLimite?.let { Text(it) } }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val valor = interpretarValor(valorLimite)
                    if (valor == null) {
                        erroLimite = "Informe um valor maior que zero, com até duas casas decimais."
                    } else {
                        onSalvarLimite(valor)
                        editarLimite = false
                    }
                }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { editarLimite = false }) { Text("Cancelar") }
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
                        Text("Atenção às despesas e dívidas", style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f).semantics { heading() })
                    }
                    Text("Algumas categorias aparecem com frequência. Que tal revisar com calma?")
                    alertas.forEach { alerta ->
                        Text("${alerta.categoria} • ${alerta.quantidade} registros",
                            style = MaterialTheme.typography.titleMedium)
                        Text(formatarMoeda(alerta.total), style = EstiloValorGrande)
                        Text("Despesas nos últimos 7 dias: ${alerta.quantidadeDespesas} • ${formatarMoeda(alerta.totalDespesas)}")
                        Text("Dívidas em aberto: ${alerta.quantidadeDividas} • ${formatarMoeda(alerta.totalDividas)}")
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
                Text("Despesas registradas: ${formatarMoeda(totais.gastos)}")
                Text("Dívidas cadastradas: ${formatarMoeda(totais.dividas)}")
                Text("Total acumulado: ${formatarMoeda(totais.acumulado)}", style = EstiloValor)
                Text("Todos os registros. Dívidas são somadas separadamente e não descontam do seu limite.",
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
                    if (limite == null) "Defina seu limite" else formatarMoeda(saldoDisponivel),
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

                TextButton(onClick = {
                    valorLimite = limite?.let { String.format(Locale("pt", "BR"), "%.2f", it) } ?: ""
                    erroLimite = null
                    editarLimite = true
                }) { Text(if (limite == null) "Definir limite" else "Alterar limite") }

                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = {progressoLimpo},
                    modifier = Modifier.fillMaxWidth().height(12.dp),
                    color = if (percentagemGasta > 0.9f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    if (despesas.isEmpty()) "Que tal registrar a sua primeira despesa?"
                    else "Você já registrou ${despesas.size} despesa(s). Bom trabalho!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val hoje = Calendar.getInstance()
        val inicio = (hoje.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            when (periodoSelecionado) {
                "Últimos 7 dias" -> add(Calendar.DAY_OF_YEAR, -6)
                "Mês atual" -> set(Calendar.DAY_OF_MONTH, 1)
            }
        }.timeInMillis
        val fimExclusivo = (hoje.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        val despesasFiltradas = despesas.mapNotNull { despesa ->
            val instante = lerDataDaDespesa(despesa.data)
            if (instante != null && instante >= inicio && instante < fimExclusivo) {
                Pair(despesa, instante)
            } else null
        }.sortedByDescending { it.second }.map { it.first }

        FluxoCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Consultar despesas", style = MaterialTheme.typography.titleLarge)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    listOf("Hoje", "Últimos 7 dias", "Mês atual").forEach { periodo ->
                        TextButton(onClick = { periodoSelecionado = periodo }) {
                            Text(if (periodoSelecionado == periodo) "✓ $periodo" else periodo)
                        }
                    }
                }
                Text(
                    "Total no período: ${formatarMoeda(despesasFiltradas.sumOf { it.valor })}",
                    style = EstiloValor
                )
                if (despesasFiltradas.isEmpty()) {
                    Text("Nenhuma despesa encontrada nesse período.")
                } else {
                    despesasFiltradas.forEach { despesa ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(despesa.titulo, style = MaterialTheme.typography.titleMedium)
                                Text(despesa.data, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(formatarMoeda(despesa.valor), style = EstiloValor)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Últimos lançamentos", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        if (despesas.isEmpty()) {
            Text(
                "Nada por aqui ainda. Suas despesas aparecerão assim que você lançar.",
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

private fun lerDataDaDespesa(texto: String): Long? {
    val formatos = listOf("dd/MM/yyyy", "yyyy-MM-dd")
    for (formato in formatos) {
        val parser = SimpleDateFormat(formato, Locale("pt", "BR"))
        parser.isLenient = false
        val posicao = ParsePosition(0)
        val data = parser.parse(texto.trim(), posicao)
        if (data != null && posicao.index == texto.trim().length) {
            return data.time
        }
    }
    return null
}
