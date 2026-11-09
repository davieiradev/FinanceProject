package br.edu.ifal.financeproject

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AtualizadorSelicTest {
    @Test
    fun compoeTaxasDiariasDesdeOPrimeiroDiaDeAtraso() {
        val divida = Divida(id = 1, titulo = "Cartão", valor = 100.0, dataVencimento = "01/11/2026")
        val taxas = listOf(
            TaxaSelicDiaria(interpretarData("03/11/2026")!!, 1.0),
            TaxaSelicDiaria(interpretarData("04/11/2026")!!, 1.0)
        )

        val resultado = calcularCorrecaoSelic(
            dividas = listOf(divida),
            taxas = taxas,
            hoje = interpretarData("05/11/2026")!!
        ).getValue(1)

        assertEquals(102.01, resultado.valorCorrigido, 0.001)
        assertEquals(2.01, resultado.juros, 0.001)
        assertEquals(2, resultado.diasComTaxa)
    }

    @Test
    fun ignoraDividaPagaOuSemAtraso() {
        val hoje = interpretarData("05/11/2026")!!
        val taxas = listOf(TaxaSelicDiaria(interpretarData("04/11/2026")!!, 1.0))
        val paga = Divida(id = 1, titulo = "Paga", valor = 100.0, dataVencimento = "01/11/2026", paga = true)
        val futura = Divida(id = 2, titulo = "Futura", valor = 100.0, dataVencimento = "05/11/2026")

        assertTrue(calcularCorrecaoSelic(listOf(paga, futura), taxas, hoje).isEmpty())
    }
}
