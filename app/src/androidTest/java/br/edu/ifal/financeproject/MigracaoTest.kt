package br.edu.ifal.financeproject

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MigracaoTest {
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
                .addMigrations(AppDataBase.MIGRATION_1_2).build()
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
