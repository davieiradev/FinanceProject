package br.edu.ifal.financeproject.dados.dao

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import br.edu.ifal.financeproject.dados.AppDataBase
import br.edu.ifal.financeproject.dados.modelo.FormaPagamento
import br.edu.ifal.financeproject.dados.modelo.Gasto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class GastoDaoTest {
    @Test fun pagamentosSaoIdempotentesEParcelasAcompanhamOGasto() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java).build()
        try {
            val dao = db.gastoDao()
            val gasto = Gasto(titulo = "Compra", totalCentavos = 10, data = "01/01/2026",
                categoria = "Outros", forma = FormaPagamento.A_PRAZO)
            assertTrue(runCatching { dao.registrar(gasto, 0, null) }.isFailure)
            assertTrue(dao.observar().first().isEmpty())
            dao.registrar(gasto, 3, "31/01/2026")
            val salvo = dao.observar().first().single()
            val id = salvo.gasto.id
            assertEquals(3, salvo.parcelas.size)
            assertEquals(1, dao.pagar(id, 1, "31/01/2026"))
            assertEquals(0, dao.pagar(id, 1, "01/02/2026"))
            assertEquals(0, dao.marcarLembrete(id, 1))
            assertEquals(1, dao.marcarLembrete(id, 2))
            assertEquals(0, dao.marcarLembrete(id, 2))
            assertEquals(3L, dao.observar().first().single().pagoCentavos)
            dao.pagar(id, 2, "28/02/2026")
            dao.pagar(id, 3, "31/03/2026")
            assertTrue(dao.observar().first().single().pago)
            dao.excluir(salvo.gasto)
            assertTrue(dao.observar().first().isEmpty())
            assertEquals(0, dao.marcarLembrete(id, 3))
        } finally {
            db.close()
        }
    }
}
