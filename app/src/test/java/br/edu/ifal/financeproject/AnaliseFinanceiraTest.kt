package br.edu.ifal.financeproject

import org.junit.Assert.*
import org.junit.Test

class AnaliseFinanceiraTest {
    private fun gasto(data: String, categoria: String = "Lazer", valor: Double = 10.0) =
        Despesa(titulo = "Teste", valor = valor, data = data, categoria = categoria)

    @Test fun totaisSomamEReagemARemocao() {
        val despesas = listOf(gasto("26/10/2026", valor = 25.0))
        val dividas = listOf(Divida(titulo = "Cartão", valor = 100.0, dataVencimento = "01/11/2026"))
        val totais = calcularTotais(despesas, dividas)
        assertEquals(25.0, totais.gastos, 0.001)
        assertEquals(100.0, totais.dividas, 0.001)
        assertEquals(125.0, totais.acumulado, 0.001)
        assertEquals(25.0, calcularTotais(despesas, emptyList()).acumulado, 0.001)
        assertEquals(0.0, calcularTotais(emptyList(), emptyList()).acumulado, 0.001)
    }

    @Test fun atalhosExigemDuasOcorrenciasELimitamTresCategorias() {
        val categorias = listOf("Lazer", "Lazer", "Lazer", "Outros", "Outros", "Educação", "Educação", "Alimentação", "Alimentação", "Transporte")
        assertEquals(listOf("Lazer", "Alimentação", "Educação"), categoriasFrequentes(categorias))
        assertTrue(categoriasFrequentes(listOf("Lazer", "Outros")).isEmpty())
        assertTrue(categoriasFrequentes(listOf("Inválida", "Inválida")).isEmpty())
    }

    @Test fun alertaRespeitaLimiarJanelaCategoriaEExclusao() {
        val hoje = interpretarData("26/10/2026")!!
        val validos = listOf(gasto("20/10/2026"), gasto("25/10/2026"), gasto("26/10/2026"))
        assertTrue(alertasRecorrencia(validos.take(2), hoje).isEmpty())
        val fora = listOf(gasto("19/10/2026"), gasto("27/10/2026"), gasto("31/02/2026")) +
            List(3) { gasto("26/10/2026", "Alimentação") }
        val alerta = alertasRecorrencia(validos + fora, hoje).single()
        assertEquals("Lazer", alerta.categoria)
        assertEquals(3, alerta.quantidade)
        assertEquals(30.0, alerta.total, 0.001)
        assertTrue(alertasRecorrencia(validos.dropLast(1) + fora, hoje).isEmpty())
    }

    @Test fun datasPrecisamExistirEEstarNoFormatoCompleto() {
        assertNotNull(interpretarData("29/02/2024"))
        listOf("29/02/2026", "31/04/2026", "1/1/2026", "26/10/2026abc", "")
            .forEach { assertNull(it, interpretarData(it)) }
    }

    @Test fun alertaCombinaGastosEDividasNaMesmaCategoria() {
        val hoje = interpretarData("26/10/2026")!!
        val despesas = listOf(gasto("26/10/2026"), gasto("25/10/2026"))
        val divida = Divida(titulo = "Parcela", valor = 100.0, dataVencimento = "10/11/2026", categoria = "Lazer")
        val alerta = alertasRecorrencia(despesas, hoje, listOf(divida)).single()
        assertEquals(3, alerta.quantidade)
        assertEquals(2, alerta.quantidadeDespesas)
        assertEquals(1, alerta.quantidadeDividas)
        assertEquals(20.0, alerta.totalDespesas, 0.001)
        assertEquals(100.0, alerta.totalDividas, 0.001)
        assertEquals(120.0, alerta.total, 0.001)
        assertTrue(alertasRecorrencia(despesas, hoje, listOf(divida.copy(paga = true))).isEmpty())
        assertTrue(alertasRecorrencia(despesas, hoje, listOf(divida.copy(categoria = "Outros"))).isEmpty())
        assertTrue(alertasRecorrencia(despesas, hoje, emptyList()).isEmpty())
    }

    @Test fun alertaTambemFuncionaSomenteComDividasAbertas() {
        val dividas = List(3) { Divida(titulo = "Parcela", valor = 50.0, dataVencimento = "10/11/2026", categoria = "Outros") }
        val alerta = alertasRecorrencia(emptyList(), dividas = dividas).single()
        assertEquals(0, alerta.quantidadeDespesas)
        assertEquals(3, alerta.quantidadeDividas)
        assertEquals(150.0, alerta.total, 0.001)
        assertTrue(alertasRecorrencia(emptyList(), dividas = dividas.take(2)).isEmpty())
        assertTrue(alertasRecorrencia(emptyList(), dividas = dividas.map { it.copy(categoria = "Educação") }).isEmpty())
    }
}
