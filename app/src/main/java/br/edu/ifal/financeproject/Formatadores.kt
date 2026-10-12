package br.edu.ifal.financeproject

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val localBR = Locale("pt", "BR")

fun interpretarValor(texto: String): Double? {
    val valor = texto.trim().let {
        if (',' in it) it.replace(".", "").replace(',', '.') else it
    }
    if (!valor.matches(Regex("[0-9]+(\\.[0-9]{1,2})?"))) return null
    return valor.toDoubleOrNull()?.takeIf { it.isFinite() && it > 0 }
}

fun formatarMoeda(valor: Double): String =
    NumberFormat.getCurrencyInstance(localBR).format(valor)

fun dataHoje(): String =
    SimpleDateFormat("dd/MM/yyyy", localBR).format(Date())
