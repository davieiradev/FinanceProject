package br.edu.ifal.financeproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Despesa::class, Divida::class, PagamentoDivida::class],
    version = 4,
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

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "banco_despesas"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
