package br.edu.ifal.financeproject.financas

import br.edu.ifal.financeproject.dados.modelo.FormaPagamento
import br.edu.ifal.financeproject.dados.modelo.Gasto
import org.junit.Assert.*
import org.junit.Test

class AnaliseFinanceiraTest {
    private fun gasto(data: String, categoria: String = "Lazer", valor: Double = 10.0) =
        Gasto(titulo = "Teste", totalCentavos = (valor * 100).toLong(), data = data,
            categoria = categoria, forma = FormaPagamento.A_VISTA)

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

    @Test fun alertaContaCadaCompraUmaVezIndependentementeDaForma() {
        val hoje = interpretarData("26/10/2026")!!
        val compra = gasto("26/10/2026")
        val alerta = alertasRecorrencia(listOf(compra, compra, compra.copy(forma = FormaPagamento.A_PRAZO)), hoje).single()
        assertEquals(3, alerta.quantidade)
        assertEquals(30.0, alerta.total, 0.001)
    }
}
