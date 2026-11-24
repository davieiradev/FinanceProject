package br.edu.ifal.financeproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Despesa::class, Divida::class, PagamentoDivida::class],
    version = 6,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun despesaDao(): DespesaDao
    abstract fun dividaDao(): DividaDao
    abstract fun pagamentoDividaDao(): PagamentoDividaDao

    companion object {
        // Mantemos as migrações existentes para preservar bancos mais antigos.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val temCategoria = db.query("PRAGMA table_info(tabela_despesas)").use { cursor ->
                    val indiceNome = cursor.getColumnIndexOrThrow("name")
                    var encontrada = false
                    while (cursor.moveToNext()) {
                        if (cursor.getString(indiceNome) == "categoria") encontrada = true
                    }
                    encontrada
                }
                if (!temCategoria) {
                    db.execSQL("ALTER TABLE tabela_despesas ADD COLUMN categoria TEXT NOT NULL DEFAULT 'Outros'")
                }
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS tabela_dividas (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "titulo TEXT NOT NULL, " +
                            "valor REAL NOT NULL, " +
                            "dataVencimento TEXT NOT NULL, " +
                            "paga INTEGER NOT NULL, " +
                            "categoria TEXT NOT NULL)"
                )
                val temCategoria = db.query("PRAGMA table_info(tabela_dividas)").use { cursor ->
                    val indiceNome = cursor.getColumnIndexOrThrow("name")
                    var encontrada = false
                    while (cursor.moveToNext()) {
                        if (cursor.getString(indiceNome) == "categoria") encontrada = true
                    }
                    encontrada
                }
                if (!temCategoria) {
                    db.execSQL("ALTER TABLE tabela_dividas ADD COLUMN categoria TEXT NOT NULL DEFAULT 'Outros'")
                }
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tabela_pagamentos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        dividaId INTEGER NOT NULL,
                        valor REAL NOT NULL,
                        dataPagamento TEXT,
                        FOREIGN KEY(dividaId) REFERENCES tabela_dividas(id)
                            ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_tabela_pagamentos_dividaId " +
                            "ON tabela_pagamentos(dividaId)"
                )
                // Dívidas antigas marcadas como pagas continuam com 100% de progresso.
                // A data dos pagamentos anteriores é desconhecida (NULL).
                db.execSQL(
                    """
                    INSERT INTO tabela_pagamentos (dividaId, valor, dataPagamento)
                    SELECT id, valor, NULL
                    FROM tabela_dividas
                    WHERE paga = 1 AND valor > 0
                    """.trimIndent()
                )
            }
        }

        // A versão 4 já possui a mesma estrutura da versão 6. Esta migração existe
        // para permitir que instalações que já estavam na versão 4 avancem sem perda
        // de dados, após a numeração ter sido corrigida.
        val MIGRATION_4_6 = object : Migration(4, 6) {
            override fun migrate(db: SupportSQLiteDatabase) = Unit
        }

        // Algumas instalações foram executadas com uma versão 5 experimental, que
        // mantinha despesas e dívidas, mas ainda não possuía a tabela de pagamentos.
        // A migração cria apenas o que falta e preserva os registros existentes.
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tabela_pagamentos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        dividaId INTEGER NOT NULL,
                        valor REAL NOT NULL,
                        dataPagamento TEXT,
                        FOREIGN KEY(dividaId) REFERENCES tabela_dividas(id)
                            ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_tabela_pagamentos_dividaId " +
                            "ON tabela_pagamentos(dividaId)"
                )
                db.execSQL(
                    """
                    INSERT INTO tabela_pagamentos (dividaId, valor, dataPagamento)
                    SELECT id, valor, NULL
                    FROM tabela_dividas
                    WHERE paga = 1
                      AND valor > 0
                      AND NOT EXISTS (
                          SELECT 1
                          FROM tabela_pagamentos
                          WHERE tabela_pagamentos.dividaId = tabela_dividas.id
                      )
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "banco_despesas"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_6,
                        MIGRATION_5_6
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
