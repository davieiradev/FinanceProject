package br.edu.ifal.financeproject

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TaxaSelicDiaria(val data: Date, val percentual: Double)

data class DividaAtualizada(
    val valorOriginal: Double,
    val juros: Double,
    val valorCorrigido: Double,
    val diasComTaxa: Int
)

private val formatoApi = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).apply {
    isLenient = false
}

private fun inicioDoDia(data: Date): Date = Calendar.getInstance().apply {
    time = data
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.time

fun primeiroDiaDeAtraso(dataVencimento: String): Date? = interpretarData(dataVencimento)?.let { vencimento ->
    Calendar.getInstance().apply {
        time = inicioDoDia(vencimento)
        add(Calendar.DAY_OF_YEAR, 1)
    }.time
}

fun estaEmAtraso(divida: Divida, hoje: Date = Date()): Boolean =
    !divida.paga && (interpretarData(divida.dataVencimento)?.before(inicioDoDia(hoje)) == true)

fun calcularCorrecaoSelic(
    dividas: List<Divida>,
    taxas: List<TaxaSelicDiaria>,
    hoje: Date = Date()
): Map<Int, DividaAtualizada> {
    val hojeSemHorario = inicioDoDia(hoje)
    return dividas.filter { estaEmAtraso(it, hojeSemHorario) }.mapNotNull { divida ->
        val inicioAtraso = primeiroDiaDeAtraso(divida.dataVencimento) ?: return@mapNotNull null
        if (!inicioAtraso.before(hojeSemHorario)) return@mapNotNull null

        // O Banco Central só publica a Selic em dias úteis; por isso contamos apenas as taxas retornadas.
        val taxasDaDivida = taxas.filter { it.data >= inicioAtraso && it.data <= hojeSemHorario }
        val fator = taxasDaDivida.fold(1.0) { acumulado, taxa -> acumulado * (1 + taxa.percentual / 100) }
        val valorCorrigido = divida.valor * fator
        divida.id to DividaAtualizada(
            valorOriginal = divida.valor,
            juros = valorCorrigido - divida.valor,
            valorCorrigido = valorCorrigido,
            diasComTaxa = taxasDaDivida.size
        )
    }.toMap()
}

class RepositorioSelic(private val api: BancoCentralApi = RetrofitBancoCentral.api) {
    suspend fun calcularDividasEmAtraso(dividas: List<Divida>): Map<Int, DividaAtualizada> {
        val hoje = inicioDoDia(Date())
        val primeiroAtraso = dividas.filter { estaEmAtraso(it, hoje) }
            .mapNotNull { primeiroDiaDeAtraso(it.dataVencimento) }
            .filter { it.before(hoje) }
            .minOrNull() ?: return emptyMap()

        val resposta = api.buscarSelicDiaria(
            dataInicial = formatoApi.format(primeiroAtraso),
            dataFinal = formatoApi.format(hoje)
        )
        val taxas = resposta.mapNotNull { taxa ->
            val data = runCatching { formatoApi.parse(taxa.data) }.getOrNull()
            val percentual = taxa.valor.replace(',', '.').toDoubleOrNull()
            if (data != null && percentual != null) TaxaSelicDiaria(data, percentual) else null
        }
        if (taxas.isEmpty()) error("O Banco Central não retornou taxas para o período consultado.")
        return calcularCorrecaoSelic(dividas, taxas, hoje)
    }
}
