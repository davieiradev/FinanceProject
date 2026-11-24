package br.edu.ifal.financeproject.financas

import br.edu.ifal.financeproject.dados.modelo.FormaPagamento
import br.edu.ifal.financeproject.dados.modelo.Gasto
import br.edu.ifal.financeproject.dados.modelo.GastoComParcelas
import org.junit.Assert.*
import org.junit.Test

class ParcelamentoTest {
    @Test fun ultimaParcelaAbsorveDiferencaEDataUsaDiaOriginal() {
        val parcelas = gerarParcelas(1, 10, 3, "31/01/2026")
        assertEquals(listOf(3L, 3L, 4L), parcelas.map { it.centavos })
        assertEquals(listOf("31/01/2026", "28/02/2026", "31/03/2026"), parcelas.map { it.vencimento })
        assertEquals("29/02/2024", gerarParcelas(1, 200, 2, "31/01/2024").last().vencimento)
    }

    @Test fun limiteEStatusAcompanhamOsPagamentos() {
        val gasto = Gasto(1, "Compra", 10, "01/01/2026", "Outros", FormaPagamento.A_PRAZO)
        val registro = GastoComParcelas(gasto, gerarParcelas(1, 10, 3, "31/01/2026"))
        assertEquals(0L, registro.pagoCentavos)
        val parcial = registro.copy(parcelas = registro.parcelas.map {
            if (it.numero == 1) it.copy(dataPagamento = "31/01/2026") else it
        })
        assertEquals(17L, 20L - parcial.pagoCentavos)
        assertFalse(parcial.pago)
        val completo = registro.copy(parcelas = registro.parcelas.map { it.copy(dataPagamento = "01/04/2026") })
        assertTrue(completo.pago)
        assertEquals(10L, completo.pagoCentavos)
        val vista = GastoComParcelas(gasto.copy(forma = FormaPagamento.A_VISTA), emptyList())
        assertTrue(vista.pago)
        assertEquals(gasto.totalCentavos, vista.pagoCentavos)
    }

    @Test fun janelaDeLembreteIncluiHojeEAtravessaViradaDoAno() {
        val hoje = interpretarData("30/12/2026")!!
        assertNull(diasAteLembrete("29/12/2026", hoje))
        assertEquals(0, diasAteLembrete("30/12/2026", hoje))
        assertEquals(3, diasAteLembrete("02/01/2027", hoje))
        assertNull(diasAteLembrete("03/01/2027", hoje))
    }
}
