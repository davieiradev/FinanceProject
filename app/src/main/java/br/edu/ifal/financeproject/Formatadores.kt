package br.edu.ifal.financeproject

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val localBR = Locale("pt", "BR")

fun formatarMoeda(valor: Double): String =
    NumberFormat.getCurrencyInstance(localBR).format(valor)

fun dataHoje(): String =
    SimpleDateFormat("dd/MM/yyyy", localBR).format(Date())