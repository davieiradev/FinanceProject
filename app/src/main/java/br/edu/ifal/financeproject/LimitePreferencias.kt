package br.edu.ifal.financeproject

import android.content.Context

class LimitePreferencias(context: Context) {
    private val preferencias = context.applicationContext
        .getSharedPreferences("configuracoes_financeiras", Context.MODE_PRIVATE)

    fun ler(): Double? = preferencias.getString("limite_gastos", null)
        ?.toDoubleOrNull()?.takeIf { it.isFinite() && it > 0 }

    fun salvar(valor: Double) {
        require(valor.isFinite() && valor > 0)
        preferencias.edit().putString("limite_gastos", valor.toString()).apply()
    }
}
