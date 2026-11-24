package br.edu.ifal.financeproject

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val categoriasDisponiveis = listOf("Alimentação", "Transporte", "Lazer", "Educação", "Outros")

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

data class TotaisFinanceiros(val gastos: Double, val dividas: Double) {
    val acumulado: Double get() = gastos + dividas
}

fun calcularTotais(despesas: List<Despesa>, dividas: List<Divida>) = TotaisFinanceiros(
    gastos = despesas.sumOf { it.valor },
    dividas = dividas.sumOf { it.valor }
)

data class AlertaRecorrencia(
    val categoria: String,
    val quantidadeDespesas: Int,
    val quantidadeDividas: Int,
    val totalDespesas: Double,
    val totalDividas: Double
) {
    val quantidade: Int get() = quantidadeDespesas + quantidadeDividas
    val total: Double get() = totalDespesas + totalDividas
}

// Janela de sete dias de calendário, incluindo hoje e excluindo datas futuras.
fun alertasRecorrencia(
    despesas: List<Despesa>,
    hoje: Date = Date(),
    dividas: List<Divida> = emptyList()
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
    val gastosRecentes = despesas.filter {
        val data = interpretarData(it.data)
        it.categoria in listOf("Lazer", "Outros") && data != null &&
            !data.before(inicio) && data.before(fim)
    }.groupBy { it.categoria }
    // Vencimento não indica quando a dívida foi contraída: considerar todas as abertas.
    val dividasAbertas = dividas.filter { !it.paga && it.categoria in listOf("Lazer", "Outros") }
        .groupBy { it.categoria }
    return (gastosRecentes.keys + dividasAbertas.keys).mapNotNull { categoria ->
        val gastos = gastosRecentes[categoria].orEmpty()
        val pendencias = dividasAbertas[categoria].orEmpty()
        if (gastos.size + pendencias.size >= 3) {
            AlertaRecorrencia(categoria, gastos.size, pendencias.size,
                gastos.sumOf { it.valor }, pendencias.sumOf { it.valor })
        } else null
    }.sortedBy { it.categoria }
}
