package br.edu.ifal.financeproject

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.ui.theme.EstiloValor

data class GastoPorCategoria(val categoria: String, val total: Double)

fun agruparGastosPorCategoria(despesas: List<Despesa>): List<GastoPorCategoria> = despesas
    .groupBy { it.categoria }
    .map { (categoria, gastos) -> GastoPorCategoria(categoria, gastos.sumOf { it.valor }) }
    .sortedByDescending { it.total }

@Composable
fun GraficoGastosPorCategoria(valores: List<GastoPorCategoria>) {
    if (valores.isEmpty()) {
        Text("Registre despesas no período escolhido para ver o gráfico.")
        return
    }

    val maiorValor = valores.maxOf { it.total }
    val corBarra = MaterialTheme.colorScheme.primary
    val corTrilha = MaterialTheme.colorScheme.surfaceVariant

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        valores.forEach { dado ->
            Column(
                modifier = Modifier.fillMaxWidth().semantics {
                    contentDescription = "${dado.categoria}: ${formatarMoeda(dado.total)}"
                },
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(dado.categoria, style = MaterialTheme.typography.titleSmall)
                    Text(formatarMoeda(dado.total), style = EstiloValor)
                }
                BarraDoGrafico(
                    proporcao = (dado.total / maiorValor).toFloat(),
                    cor = corBarra,
                    trilha = corTrilha
                )
            }
        }
    }
}

@Composable
private fun BarraDoGrafico(proporcao: Float, cor: Color, trilha: Color) {
    Canvas(modifier = Modifier.fillMaxWidth().height(18.dp)) {
        val raio = size.height / 2
        drawRoundRect(color = trilha, cornerRadius = androidx.compose.ui.geometry.CornerRadius(raio, raio))
        drawRoundRect(
            color = cor,
            size = size.copy(width = size.width * proporcao.coerceIn(0f, 1f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(raio, raio)
        )
    }
}
