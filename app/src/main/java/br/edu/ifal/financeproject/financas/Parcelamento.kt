package br.edu.ifal.financeproject.financas

import br.edu.ifal.financeproject.dados.modelo.Parcela
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun gerarParcelas(gastoId: Long, total: Long, quantidade: Int, primeira: String): List<Parcela> {
    require(quantidade > 0 && total >= quantidade)
    val ancora = Calendar.getInstance().apply { time = requireNotNull(interpretarData(primeira)) }
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    val mesesDisponiveis = (9999 - ancora.get(Calendar.YEAR)) * 12 + 12 - ancora.get(Calendar.MONTH)
    require(quantidade <= mesesDisponiveis) { "O último vencimento deve ocorrer até o ano 9999." }
    val base = total / quantidade
    return List(quantidade) { indice ->
        val mes = (ancora.clone() as Calendar).apply { add(Calendar.MONTH, indice) }
        val valor = if (indice == quantidade - 1) base + total % quantidade else base
        Parcela(gastoId, indice + 1, valor, formato.format(mes.time))
    }
}

fun diasAteLembrete(vencimento: String, hoje: Date = Date()): Int? {
    val data = Calendar.getInstance().apply { time = hoje }
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    for (dias in 0..3) {
        if (formato.format(data.time) == vencimento) return dias
        data.add(Calendar.DAY_OF_MONTH, 1)
    }
    return null
}
