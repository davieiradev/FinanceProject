package br.edu.ifal.financeproject.ui.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.dados.modelo.FormaPagamento
import br.edu.ifal.financeproject.dados.modelo.GastoComParcelas
import br.edu.ifal.financeproject.dados.modelo.Parcela
import br.edu.ifal.financeproject.financas.formatarMoeda
import br.edu.ifal.financeproject.financas.interpretarData
import br.edu.ifal.financeproject.ui.componentes.GraficoGastosPorCategoria
import br.edu.ifal.financeproject.ui.componentes.agruparGastosPorCategoria
import br.edu.ifal.financeproject.ui.theme.FluxoCard
import java.util.Calendar

@Composable
fun TelaGastos(
    registros: List<GastoComParcelas>,
    onExcluir: (GastoComParcelas) -> Unit,
    onPagar: (Parcela) -> Unit,
    modifier: Modifier = Modifier
) {
    var excluir by remember { mutableStateOf<GastoComParcelas?>(null) }
    var pagar by remember { mutableStateOf<Parcela?>(null) }
    var periodoSelecionado by rememberSaveable { mutableStateOf("Todos") }
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Meus gastos", style = MaterialTheme.typography.headlineMedium)
        val hoje = Calendar.getInstance()
        val inicio = (hoje.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            when (periodoSelecionado) {
                "Semana" -> add(Calendar.DAY_OF_YEAR, -6)
                "Mês" -> set(Calendar.DAY_OF_MONTH, 1)
            }
        }.timeInMillis
        val fimExclusivo = (hoje.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis
        val filtrados = if (periodoSelecionado == "Todos") registros else registros.filter {
            val instante = interpretarData(it.gasto.data)?.time
            instante != null && instante >= inicio && instante < fimExclusivo
        }
        if (registros.isEmpty()) Text("Toque em Lançar para registrar seu primeiro gasto.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Text("Consultar gastos", style = MaterialTheme.typography.titleLarge)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Todos", "Hoje", "Semana", "Mês").forEach { periodo ->
                        FilterChip(selected = periodoSelecionado == periodo,
                            onClick = { periodoSelecionado = periodo }, label = { Text(periodo.uppercase()) })
                    }
                }
                Text("Total no período: ${formatarMoeda(filtrados.sumOf { it.gasto.valor })}")
            }
            item {
                FluxoCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Gastos por categoria", style = MaterialTheme.typography.titleMedium)
                        GraficoGastosPorCategoria(agruparGastosPorCategoria(filtrados.map { it.gasto }))
                    }
                }
            }
            items(filtrados, key = { it.gasto.id }) { registro ->
                val gasto = registro.gasto
                var expandido by rememberSaveable { mutableStateOf(false) }
                FluxoCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().clickable(enabled = registro.parcelas.isNotEmpty(),
                        onClickLabel = if (expandido) "Recolher" else "Ver parcelas") { expandido = !expandido }.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(gasto.titulo, style = MaterialTheme.typography.titleMedium)
                        if (expandido || registro.parcelas.isEmpty()) Text("${gasto.categoria} • ${gasto.data}")
                        val forma = if (gasto.forma == FormaPagamento.A_VISTA) "À vista" else "A prazo"
                        Text(formatarMoeda(gasto.valor), style = MaterialTheme.typography.headlineSmall)
                        Text(forma, style = MaterialTheme.typography.labelMedium)
                        Text(if (registro.pago) "Pago" else "Em aberto")
                        if (registro.parcelas.isNotEmpty()) {
                            HorizontalDivider()
                            val pagas = registro.parcelas.count { it.dataPagamento != null }
                            Text("$pagas de ${registro.parcelas.size} parcelas pagas", style = MaterialTheme.typography.titleSmall)
                            LinearProgressIndicator(progress = { (registro.pagoCentavos.toDouble() / gasto.totalCentavos).toFloat() },
                                modifier = Modifier.fillMaxWidth().height(12.dp), color = MaterialTheme.colorScheme.tertiary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant)
                            OutlinedButton(onClick = { expandido = !expandido }) {
                                Text(if (expandido) "RECOLHER" else "VER PARCELAS")
                            }
                            if (expandido) {
                                Text(if (gasto.jurosCentavos > 0) "Inclui ${formatarMoeda(gasto.jurosCentavos / 100.0)} de juros" else "Sem juros")
                                registro.parcelas.filter { it.dataPagamento == null }.minByOrNull { it.numero }?.let { proxima ->
                                    Text("Próxima parcela: ${proxima.vencimento} • ${formatarMoeda(proxima.centavos / 100.0)}")
                                }
                                Text("Pago: ${formatarMoeda(registro.pagoCentavos / 100.0)}")
                                Text("Falta pagar: ${formatarMoeda((gasto.totalCentavos - registro.pagoCentavos) / 100.0)}")
                                registro.parcelas.sortedBy { it.numero }.forEach { parcela ->
                                    HorizontalDivider()
                                    Text("Parcela ${parcela.numero} • ${formatarMoeda(parcela.centavos / 100.0)}")
                                    Text("Vencimento: ${parcela.vencimento}")
                                    if (parcela.dataPagamento != null) Text("Paga em ${parcela.dataPagamento}")
                                    else Button(onClick = { pagar = parcela }) { Text("REGISTRAR PAGAMENTO") }
                                }
                            }
                        }
                        if (expandido || registro.parcelas.isEmpty()) OutlinedButton(onClick = { excluir = registro }) { Text("EXCLUIR GASTO") }
                    }
                }
            }
        }
    }
    pagar?.let { parcela ->
        AlertDialog(onDismissRequest = { pagar = null }, title = { Text("Registrar pagamento?") },
            text = { Text("Parcela ${parcela.numero} de ${formatarMoeda(parcela.centavos / 100.0)} com vencimento em ${parcela.vencimento}.") },
            confirmButton = { Button(onClick = { onPagar(parcela); pagar = null }) { Text("Confirmar") } },
            dismissButton = { OutlinedButton(onClick = { pagar = null }) { Text("Cancelar") } })
    }
    excluir?.let { registro ->
        AlertDialog(onDismissRequest = { excluir = null }, title = { Text("Excluir gasto?") },
            text = { Text("Excluir ${registro.gasto.titulo} e os registros de pagamento?") },
            confirmButton = { Button(onClick = { onExcluir(registro); excluir = null }) { Text("Excluir") } },
            dismissButton = { OutlinedButton(onClick = { excluir = null }) { Text("Cancelar") } })
    }
}
