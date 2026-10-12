package br.edu.ifal.financeproject

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValorTest {
    @Test
    fun aceitaValoresMonetarios() {
        assertEquals(1500.50, interpretarValor("1.500,50")!!, 0.001)
        assertEquals(1500.50, interpretarValor("1500.50")!!, 0.001)
        assertEquals(0.01, interpretarValor("0,01")!!, 0.001)
    }

    @Test
    fun rejeitaValoresInvalidos() {
        listOf("", "0", "-10", "NaN", "Infinity", "1e309", "abc", "1,234")
            .forEach { assertNull(it, interpretarValor(it)) }
    }
}
