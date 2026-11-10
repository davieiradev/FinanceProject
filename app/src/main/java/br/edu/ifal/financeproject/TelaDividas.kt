package br.edu.ifal.financeproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.roundToLong

@Composable
fun TelaDividas(
    dividas: List<Divida>,
    pagamentos: List<PagamentoDivida>,
    onSalvar: (Divida) -> Unit,
    onExcluir: (Divida) -> Unit,
    onRegistrarPagamento: (dividaId: Int, valor: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var valor by rememberSaveable { mutableStateOf("") }
    var dataVencimento by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("Outros") }
    var erro by rememberSaveable { mutableStateOf<String?>(null) }

    var paraExcluir by remember { mutableStateOf<Divida?>(null) }
    var paraPagar by remember { mutableStateOf<Divida?>(null) }
    var valorPagamento by rememberSaveable { mutableStateOf("") }
    var erroPagamento by rememberSaveable { mutableStateOf<String?>(null) }
    var historicoExpandidoId by rememberSaveable { mutableStateOf<Int?>(null) }
    val pagamentosPorDivida = remember(pagamentos) { pagamentos.groupBy { it.dividaId } }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("Nova Dívida", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Descrição (Ex: Cartão de Crédito)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor total (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = dataVencimento,
            onValueChange = { dataVencimento = it },
            label = { Text("Vencimento (dd/mm/aaaa)") },
            modifier = Modifier.fillMaxWidth()
        )

        SeletorCategoria(categoria, categoriasFrequentes(dividas.map { it.categoria })) {
            categoria = it
        }

        if (erro != null) {
            Text(
                erro!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val valorNumerico = interpretarValor(valor)
                if (titulo.isBlank() || valorNumerico == null || interpretarData(dataVencimento) == null) {
                    erro = "Informe descrição, valor maior que zero e vencimento válido (dd/mm/aaaa)."
                } else {
                    erro = null
                    onSalvar(
                        Divida(
                            titulo = titulo.trim(),
                            valor = valorNumerico,
                            dataVencimento = dataVencimento.trim(),
                            categoria = categoria
                        )
                    )
                    titulo = ""
                    valor = ""
                    dataVencimento = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Dívida")
        }

        Spacer(Modifier.height(24.dp))
        Text("Minhas Dívidas", style = MaterialTheme.typography.titleMedium)
        Text("Valor original total: ${formatarMoeda(dividas.sumOf { it.valor })}")
        Spacer(Modifier.height(8.dp))

        if (dividas.isEmpty()) {
            Text(
                "Nenhuma dívida cadastrada. Que alívio!",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            dividas.forEach { divida ->
                val historico = pagamentosPorDivida[divida.id].orEmpty()
                val pagoCentavos = (historico.sumOf { it.valor } * 100.0).roundToLong()
                val totalCentavos = (divida.valor * 100.0).roundToLong()
                val restanteCentavos = (totalCentavos - pagoCentavos).coerceAtLeast(0L)
                val totalPago = pagoCentavos / 100.0
                val saldoRestante = restanteCentavos / 100.0
                val progresso = if (totalCentavos > 0L) {
                    (pagoCentavos.toFloat() / totalCentavos).coerceIn(0f, 1f)
                } else 0f
                val quitada = divida.paga || restanteCentavos == 0L

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(divida.titulo, style = MaterialTheme.typography.titleMedium)
                                Text("Vence em ${divida.dataVencimento}", style = MaterialTheme.typography.bodySmall)
                                Text(divida.categoria, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(formatarMoeda(divida.valor), style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { paraExcluir = divida }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Excluir dívida",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Text("Já pago: ${formatarMoeda(totalPago)}")
                        Text("Falta pagar: ${formatarMoeda(saldoRestante)}")
                        LinearProgressIndicator(
                            progress = { progresso },
                            modifier = Modifier.fillMaxWidth().height(10.dp)
                        )
                        Text("${(progresso * 100).toInt()}% quitado")

                        if (quitada) {
                            Text("Dívida quitada!", color = MaterialTheme.colorScheme.primary)
                        } else {
                            Button(
                                onClick = {
                                    valorPagamento = ""
                                    erroPagamento = null
                                    paraPagar = divida
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Registrar pagamento")
                            }
                        }

                        if (historico.isNotEmpty()) {
                            TextButton(onClick = {
                                historicoExpandidoId = if (historicoExpandidoId == divida.id) {
                                    null
                                } else divida.id
                            }) {
                                Text(
                                    if (historicoExpandidoId == divida.id) "Ocultar histórico"
                                    else "Ver histórico (${historico.size})"
                                )
                            }
                            if (historicoExpandidoId == divida.id) {
                                historico.forEach { pagamento ->
                                    Text(
                                        "${formatarMoeda(pagamento.valor)} • " +
                                                (pagamento.dataPagamento ?: "Data anterior não registrada"),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    paraPagar?.let { divida ->
        val pago = pagamentosPorDivida[divida.id].orEmpty().sumOf { it.valor }
        val restanteCentavos = ((divida.valor - pago) * 100.0).roundToLong().coerceAtLeast(0L)
        AlertDialog(
            onDismissRequest = { paraPagar = null },
            title = { Text("Pagamento: ${divida.titulo}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Saldo restante: ${formatarMoeda(restanteCentavos / 100.0)}")
                    Text("A data de hoje será registrada automaticamente.")
                    OutlinedTextField(
                        value = valorPagamento,
                        onValueChange = {
                            valorPagamento = it
                            erroPagamento = null
                        },
                        label = { Text("Valor do pagamento (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = erroPagamento != null,
                        supportingText = { erroPagamento?.let { Text(it) } },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val valorNumerico = interpretarValor(valorPagamento)
                    val centavos = valorNumerico?.let { (it * 100.0).roundToLong() }
                    when {
                        valorNumerico == null || centavos == null || centavos <= 0L -> {
                            erroPagamento = "Informe um valor válido, maior que zero."
                        }
                        centavos > restanteCentavos -> {
                            erroPagamento = "O valor supera o saldo restante."
                        }
                        else -> {
                            onRegistrarPagamento(divida.id, valorNumerico)
                            paraPagar = null
                            valorPagamento = ""
                            erroPagamento = null
                        }
                    }
                }) { Text("Registrar") }
            },
            dismissButton = {
                TextButton(onClick = { paraPagar = null }) { Text("Cancelar") }
            }
        )
    }

    paraExcluir?.let { divida ->
        AlertDialog(
            onDismissRequest = { paraExcluir = null },
            title = { Text("Excluir dívida?") },
            text = { Text("${divida.titulo} e seus pagamentos serão removidos da lista.") },
            confirmButton = {
                TextButton(onClick = {
                    onExcluir(divida)
                    paraExcluir = null
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { paraExcluir = null }) { Text("Cancelar") }
            }
        )
    }
}
