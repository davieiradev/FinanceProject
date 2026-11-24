package br.edu.ifal.financeproject.financas

import br.edu.ifal.financeproject.dados.modelo.Gasto
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val categoriasDisponiveis = listOf("Alimentação", "Transporte", "Lazer", "Educação", "Saúde", "Outros")

fun interpretarData(texto: String): Date? {
    val entrada = texto.trim()
    if (!entrada.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) return null
    val posicao = ParsePosition(0)
    return SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).apply {
        isLenient = false
    }.parse(entrada, posicao)?.takeIf { posicao.index == entrada.length }
}

fun categoriasFrequentes(categorias: List<String>): List<String> = categorias
    .filter { it in categoriasDisponiveis }
    .groupingBy { it }.eachCount().entries
    .filter { it.value >= 2 }
    .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
    .take(3).map { it.key }

data class AlertaRecorrencia(val categoria: String, val quantidade: Int, val total: Double)

// Janela de sete dias de calendário, incluindo hoje e excluindo datas futuras.
fun alertasRecorrencia(
    gastos: List<Gasto>,
    hoje: Date = Date()
): List<AlertaRecorrencia> {
    val inicio = Calendar.getInstance().apply {
        time = hoje
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, -6)
    }.time
    val fim = Calendar.getInstance().apply {
        time = hoje
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, 1)
    }.time
    val gastosRecentes = gastos.filter {
        val data = interpretarData(it.data)
        it.categoria in listOf("Lazer", "Outros") && data != null &&
            !data.before(inicio) && data.before(fim)
    }.groupBy { it.categoria }
    return gastosRecentes.filterValues { it.size >= 3 }.map { (categoria, itens) ->
        AlertaRecorrencia(categoria, itens.size, itens.sumOf { it.valor })
    }.sortedBy { it.categoria }
}
