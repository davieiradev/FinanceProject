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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import br.edu.ifal.financeproject.ui.theme.EstiloValor
import br.edu.ifal.financeproject.ui.theme.EstiloValorGrande
import br.edu.ifal.financeproject.ui.theme.FluxoCard
import br.edu.ifal.financeproject.ui.theme.LogoFluxo

@Composable
fun TelaResumo(
    despesas: List<Despesa>,
    limite: Double?,
    onSalvarLimite: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var editarLimite by rememberSaveable { mutableStateOf(false) }
    var valorLimite by rememberSaveable { mutableStateOf("") }
    var erroLimite by rememberSaveable { mutableStateOf<String?>(null) }

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
