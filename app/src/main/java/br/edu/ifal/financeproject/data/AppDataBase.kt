package br.edu.ifal.financeproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.edu.ifal.financeproject.CarteiraCripto
import br.edu.ifal.financeproject.CarteiraCriptoDao
import br.edu.ifal.financeproject.model.Despesa
import br.edu.ifal.financeproject.model.Divida
import br.edu.ifal.financeproject.model.DicaFinanceira

@Database(entities = [Despesa::class, Divida::class, CarteiraCripto::class, DicaFinanceira::class], version = 5, exportSchema = false)

abstract class AppDataBase : RoomDatabase() {
    abstract fun despesaDao() : DespesaDao
    abstract fun carteiraCriptoDao(): CarteiraCriptoDao
    abstract fun dividaDao() : DividaDao
    abstract fun dicaFinanceiraDao(): DicaFinanceiraDao
    companion object{
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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS tabela_carteira_cripto (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                simbolo TEXT NOT NULL,
                nome TEXT NOT NULL,
                quantidade REAL NOT NULL,
                precoMedioCompra REAL NOT NULL,
                cotacaoReferencia REAL NOT NULL
            )
            """.trimIndent()
                )
            }
        }
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS tabela_dicas_financeiras (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titulo TEXT NOT NULL, categoria TEXT NOT NULL, descricao TEXT NOT NULL)"
                )
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
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
