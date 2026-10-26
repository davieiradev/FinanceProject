package br.edu.ifal.financeproject

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MigracaoTest {
    @Test
    fun preservaVersaoDoisComESemDividasEPermiteReabrir() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (temDividas in listOf(false, true)) {
            val nome = "teste_v2_$temDividas"
            context.deleteDatabase(nome)
            context.openOrCreateDatabase(nome, 0, null).use { db ->
                db.execSQL("CREATE TABLE tabela_despesas (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titulo TEXT NOT NULL, valor REAL NOT NULL, data TEXT NOT NULL, categoria TEXT NOT NULL)")
                db.execSQL("INSERT INTO tabela_despesas VALUES (1, 'Mercado', 25.0, '26/10/2026', 'Alimentação')")
                if (temDividas) {
                    db.execSQL("CREATE TABLE tabela_dividas (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titulo TEXT NOT NULL, valor REAL NOT NULL, dataVencimento TEXT NOT NULL, paga INTEGER NOT NULL)")
                    db.execSQL("INSERT INTO tabela_dividas VALUES (1, 'Cartão', 100.0, '01/11/2026', 0)")
                }
                db.version = 2
            }
            fun abrir() = Room.databaseBuilder(context, AppDataBase::class.java, nome)
                .addMigrations(AppDataBase.MIGRATION_2_3).build()
            var banco = abrir()
            try {
                assertEquals("Mercado", banco.despesaDao().listarTodas().first().single().titulo)
                if (temDividas) {
                    val antiga = banco.dividaDao().listarTodas().first().single()
                    assertEquals("Outros", antiga.categoria)
                    assertEquals(100.0, antiga.valor, 0.001)
                }
                banco.dividaDao().inserir(Divida(titulo = "Curso", valor = 50.0,
                    dataVencimento = "05/11/2026", categoria = "Educação"))
                banco.close()
                banco = abrir()
                val nova = banco.dividaDao().listarTodas().first().first { it.titulo == "Curso" }
                assertEquals("Educação", nova.categoria)
                banco.dividaDao().deletar(nova)
                assertEquals(if (temDividas) 1 else 0, banco.dividaDao().listarTodas().first().size)
            } finally {
                banco.close()
                context.deleteDatabase(nome)
            }
        }
    }

    @Test
    fun preservaDespesasDaVersaoUmComESemCategoria() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (temCategoria in listOf(false, true)) {
            val nome = "teste_migracao_$temCategoria"
            context.deleteDatabase(nome)
            context.openOrCreateDatabase(nome, 0, null).use { db ->
                val coluna = if (temCategoria) ", categoria TEXT NOT NULL" else ""
                db.execSQL("CREATE TABLE tabela_despesas (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titulo TEXT NOT NULL, valor REAL NOT NULL, data TEXT NOT NULL$coluna)")
                val categoria = if (temCategoria) ", 'Lazer'" else ""
                db.execSQL("INSERT INTO tabela_despesas VALUES (1, 'Cinema', 30.0, '12/10/2026'$categoria)")
                db.version = 1
            }
            val banco = Room.databaseBuilder(context, AppDataBase::class.java, nome)
                .addMigrations(AppDataBase.MIGRATION_1_2, AppDataBase.MIGRATION_2_3).build()
            try {
                val despesa = banco.despesaDao().listarTodas().first().single()
                assertEquals("Cinema", despesa.titulo)
                assertEquals(30.0, despesa.valor, 0.001)
                assertEquals(if (temCategoria) "Lazer" else "Outros", despesa.categoria)
            } finally {
                banco.close()
                context.deleteDatabase(nome)
            }
        }
    }
}
