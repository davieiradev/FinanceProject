package br.edu.ifal.financeproject.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.financas.ResultadoJuros
import br.edu.ifal.financeproject.financas.calcularJurosCompostos
import java.text.NumberFormat
import java.util.Locale

private fun dinheiroSimulado(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)

@Composable
fun SimuladorJurosDialog(onFechar: () -> Unit) {
    // Apenas estado temporário de interface: sem Room, preferências ou API.
    var valorTexto by remember { mutableStateOf("") }
    var taxaTexto by remember { mutableStateOf("") }
    var prazoTexto by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var resultado by remember { mutableStateOf<ResultadoJuros?>(null) }

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Simular juros") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoDinheiro("Valor inicial", valorTexto) { valorTexto = it; erro = null; resultado = null }
                OutlinedTextField(
                    value = taxaTexto,
                    onValueChange = { taxaTexto = it; erro = null; resultado = null },
                    label = { Text("Taxa ao mês (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = prazoTexto,
                    onValueChange = { prazoTexto = it; erro = null; resultado = null },
                    label = { Text("Prazo (meses)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (erro != null) {
                    Text(erro!!, color = MaterialTheme.colorScheme.error)
                }
                resultado?.let { r ->
                    Text("Total: ${dinheiroSimulado(r.total)}", style = MaterialTheme.typography.titleMedium)
                    Text("Juros: ${dinheiroSimulado(r.totalJuros)}", style = MaterialTheme.typography.titleMedium)
                    Text("Evolução mês a mês", style = MaterialTheme.typography.titleSmall)
                    r.evolucao.forEach { mes ->
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Mês ${mes.mes}")
                                Text(dinheiroSimulado(mes.totalAcumulado))
                            }
                            Text(
                                "Juros deste mês: ${dinheiroSimulado(mes.jurosDoMes)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Text(
                        "Valores estimados com capitalização mensal, sem pagamentos durante o prazo. " +
                                "Não representam parcelas nem incluem outras taxas.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(onClick = {
                val valor = valorTexto.toLongOrNull()?.div(100.0)
                val taxa = taxaTexto.trim().replace(",", ".").toDoubleOrNull()
                val prazo = prazoTexto.trim().toIntOrNull()
                erro = when {
                    valor == null || !valor.isFinite() || valor <= 0.0 ->
                        "Informe um valor maior que zero."
                    taxa == null || !taxa.isFinite() || taxa <= 0.0 || taxa > 20.0 ->
                        "A taxa deve ser maior que 0% e de até 20% ao mês."
                    prazo == null || prazo !in 1..60 ->
                        "Informe um prazo inteiro de 1 a 60 meses."
                    else -> null
                }
                if (erro == null) {
                    try {
                        resultado = calcularJurosCompostos(valor!!, taxa!!, prazo!!)
                    } catch (e: IllegalArgumentException) {
                        erro = e.message ?: "Não foi possível calcular."
                    }
                }
            }) { Text("SIMULAR") }
        },
        dismissButton = { OutlinedButton(onClick = onFechar) { Text("Fechar") } }
    )
}
