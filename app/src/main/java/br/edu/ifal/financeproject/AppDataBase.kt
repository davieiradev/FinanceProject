package br.edu.ifal.financeproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Despesa::class, Divida::class], version = 3, exportSchema = false)

abstract class AppDataBase : RoomDatabase() {
    abstract fun despesaDao() : DespesaDao

    abstract fun dividaDao() : DividaDao
    companion object{
        // A versão 2 pode existir com ou sem a tabela de dívidas.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS tabela_dividas (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titulo TEXT NOT NULL, valor REAL NOT NULL, dataVencimento TEXT NOT NULL, paga INTEGER NOT NULL, categoria TEXT NOT NULL)")
                val temCategoria = db.query("PRAGMA table_info(tabela_dividas)").use { cursor ->
                    val nome = cursor.getColumnIndexOrThrow("name")
                    var encontrada = false
                    while (cursor.moveToNext()) {
                        if (cursor.getString(nome) == "categoria") encontrada = true
                    }
                    encontrada
                }
                if (!temCategoria) {
                    db.execSQL("ALTER TABLE tabela_dividas ADD COLUMN categoria TEXT NOT NULL DEFAULT 'Outros'")
                }
            }
        }
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
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "banco_despesas"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
