package br.edu.ifal.financeproject.ui.telas

import android.app.DatePickerDialog
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
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.dados.modelo.FormaPagamento
import br.edu.ifal.financeproject.dados.modelo.Gasto
import br.edu.ifal.financeproject.financas.dataHoje
import br.edu.ifal.financeproject.financas.formatarMoeda
import br.edu.ifal.financeproject.financas.interpretarData
import br.edu.ifal.financeproject.ui.componentes.CampoDinheiro
import br.edu.ifal.financeproject.ui.componentes.SeletorCategoria
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun TelaLancamento(
    onSalvar: suspend (Gasto, Int, String?) -> Unit,
    categoriasMaisUsadas: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var valor by rememberSaveable { mutableStateOf("") }
    var data by rememberSaveable { mutableStateOf(dataHoje()) }
    var erro by rememberSaveable { mutableStateOf<String?>(null) }
    var categoria by rememberSaveable{mutableStateOf("Outros")}

    var forma by rememberSaveable { mutableStateOf(FormaPagamento.A_VISTA) }
    var parcelas by rememberSaveable { mutableStateOf("") }
    var vencimento by rememberSaveable { mutableStateOf<String?>(null) }
    var salvando by androidx.compose.runtime.remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var comJuros by rememberSaveable { mutableStateOf(false) }
    var jurosTexto by rememberSaveable { mutableStateOf("") }
    val principal = valor.toLongOrNull()?.takeIf { it > 0 }
    val incluiJuros = forma == FormaPagamento.A_PRAZO && comJuros
    val juros = if (incluiJuros) jurosTexto.toLongOrNull()?.takeIf { it > 0 } else 0L
    val centavos = if (principal != null && juros != null && principal <= Long.MAX_VALUE - juros) principal + juros else null
    val quantidade = parcelas.toIntOrNull()

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Lançar gasto", style = MaterialTheme.typography.headlineMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Descrição") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        CampoDinheiro(if (incluiJuros) "Valor sem juros" else "Valor total", valor) { valor = it }

        SeletorCategoria(categoria, categoriasMaisUsadas) { categoria = it }

        Text("Forma de pagamento", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = forma == FormaPagamento.A_VISTA,
                onClick = { forma = FormaPagamento.A_VISTA }, label = { Text("À vista") })
            FilterChip(selected = forma == FormaPagamento.A_PRAZO,
                onClick = { forma = FormaPagamento.A_PRAZO }, label = { Text("A prazo") })
        }

        if (forma == FormaPagamento.A_PRAZO) {
            Text("Juros da compra", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !comJuros, onClick = { comJuros = false }, label = { Text("Sem juros") })
                FilterChip(selected = comJuros, onClick = { comJuros = true }, label = { Text("Com juros") })
            }
            if (comJuros) {
                CampoDinheiro("Juros totais da compra", jurosTexto) { jurosTexto = it }
                centavos?.let { Text("Total com juros: ${formatarMoeda(it / 100.0)}") }
            }
            OutlinedTextField(value = parcelas, onValueChange = { parcelas = it },
                label = { Text("Número de parcelas") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth())
            if (centavos != null && quantidade != null && quantidade > 0 && centavos >= quantidade) {
                val base = centavos / quantidade
                Text("Valor da parcela: ${formatarMoeda(base / 100.0)}")
                if (centavos % quantidade != 0L) {
                    Text("Última parcela: ${formatarMoeda((base + centavos % quantidade) / 100.0)}")
                }
            }
            CampoCalendario("Vencimento da primeira parcela", vencimento) { vencimento = it }
            Text("As próximas parcelas vencem no mesmo dia ou no último dia do mês.")
        } else {
            CampoCalendario("Data do gasto", data) { data = it }
        }

        if (erro != null) {
            Spacer(Modifier.height(8.dp))
            Text(erro!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            enabled = !salvando,
            onClick = {
                val pagamento = forma
                val aPrazo = pagamento == FormaPagamento.A_PRAZO
                erro = when {
                    titulo.isBlank() -> "Informe uma descrição."
                    juros == null -> "Informe o valor total dos juros."
                    centavos == null -> "Informe um valor válido, com até duas casas decimais."
                    interpretarData(data) == null -> "Informe uma data válida (dd/mm/aaaa)."
                    aPrazo && (quantidade == null || quantidade <= 0 || quantidade > centavos) ->
                        "Informe um número de parcelas válido. Cada parcela deve ter pelo menos um centavo."
                    aPrazo && vencimento == null -> "Escolha o vencimento da primeira parcela."
                    else -> {
                        val gasto = Gasto(titulo = titulo.trim(), totalCentavos = centavos,
                            data = if (aPrazo) dataHoje() else data.trim(), categoria = categoria, forma = pagamento, jurosCentavos = juros)
                        val numero = if (aPrazo) requireNotNull(quantidade) else 0
                        val primeira = if (aPrazo) vencimento else null
                        salvando = true
                        scope.launch {
                            try {
                                onSalvar(gasto, numero, primeira)
                            } catch (e: IllegalArgumentException) {
                                erro = e.message ?: "Confira os dados do gasto."
                            } catch (e: android.database.SQLException) {
                                erro = "Não foi possível guardar o gasto. Tente novamente."
                            } finally {
                                salvando = false
                            }
                        }
                        null
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("GUARDAR GASTO")
        }
    }
}

@Composable
private fun CampoCalendario(rotulo: String, valor: String?, aoSelecionar: (String) -> Unit) {
    val context = LocalContext.current
    androidx.compose.material3.OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
        val inicial = Calendar.getInstance()
        valor?.let { interpretarData(it) }?.let { inicial.time = it }
        DatePickerDialog(context, { _, ano, mes, dia ->
            aoSelecionar(String.format(Locale.ROOT, "%02d/%02d/%04d", dia, mes + 1, ano))
        }, inicial.get(Calendar.YEAR), inicial.get(Calendar.MONTH), inicial.get(Calendar.DAY_OF_MONTH)).show()
    }) { Text(valor?.let { "$rotulo: $it" } ?: rotulo) }
}
