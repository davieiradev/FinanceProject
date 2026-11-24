package br.edu.ifal.financeproject.dados

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.edu.ifal.financeproject.dados.dao.GastoDao
import br.edu.ifal.financeproject.dados.dao.DicaFinanceiraDao
import br.edu.ifal.financeproject.dados.dao.ProgressoLicaoDao
import br.edu.ifal.financeproject.dados.modelo.DicaFinanceira
import br.edu.ifal.financeproject.dados.modelo.Gasto
import br.edu.ifal.financeproject.dados.modelo.Parcela
import br.edu.ifal.financeproject.dados.modelo.ProgressoLicao

@Database(
    entities = [Gasto::class, Parcela::class, ProgressoLicao::class, DicaFinanceira::class],
    version = 10,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun gastoDao(): GastoDao
    abstract fun progressoLicaoDao(): ProgressoLicaoDao
    abstract fun dicaFinanceiraDao(): DicaFinanceiraDao

    companion object {
        @Volatile private var instance: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, "banco_despesas")
                .addMigrations(
                    object : androidx.room.migration.Migration(8, 9) {
                        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            db.execSQL("ALTER TABLE gastos ADD COLUMN jurosCentavos INTEGER NOT NULL DEFAULT 0")
                        }
                    },
                    object : androidx.room.migration.Migration(9, 10) {
                        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            criarTabelaEInserirDicas(db)
                        }
                    }
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        criarTabelaEInserirDicas(db)
                    }

                    override fun onDestructiveMigration(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        // O Room 2.6 remove apenas tabelas conhecidas pelo novo modelo.
                        db.execSQL("DROP TABLE IF EXISTS tabela_pagamentos")
                        db.execSQL("DROP TABLE IF EXISTS tabela_dividas")
                        db.execSQL("DROP TABLE IF EXISTS tabela_despesas")
                    }
                })
                .build().also { instance = it }
        }

        private fun criarTabelaEInserirDicas(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS dicas_financeiras (
                    id INTEGER NOT NULL,
                    titulo TEXT NOT NULL,
                    categoria TEXT NOT NULL,
                    descricao TEXT NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
            )
            dicasIniciais.forEach { dica ->
                db.execSQL(
                    "INSERT OR IGNORE INTO dicas_financeiras (id, titulo, categoria, descricao) VALUES (?, ?, ?, ?)",
                    arrayOf(dica.id, dica.titulo, dica.categoria, dica.descricao)
                )
            }
        }

        private val dicasIniciais = listOf(
            DicaFinanceira(1, "Registre todos os gastos", "Organização", "Anote até os pequenos valores. Um café ou uma entrega parecem pouco isoladamente, mas a soma mostra para onde o dinheiro está indo e ajuda a ajustar o orçamento."),
            DicaFinanceira(2, "Defina um limite realista", "Planejamento", "Use a média dos seus gastos e da sua renda para criar um limite possível de cumprir. Revise esse valor quando sua rotina ou seus custos mudarem."),
            DicaFinanceira(3, "Separe uma reserva", "Reserva", "Guarde um valor recorrente, mesmo que pequeno, para imprevistos. Uma reserva evita recorrer a empréstimos ou ao cartão quando surge uma despesa urgente."),
            DicaFinanceira(4, "Compare antes de comprar", "Consumo consciente", "Pesquise preços, condições de pagamento e necessidade da compra. Esperar um dia antes de finalizar uma compra por impulso pode ajudar na decisão."),
            DicaFinanceira(5, "Pague antes do vencimento", "Dívidas", "Atrasos podem gerar multa e juros. Acompanhe as parcelas cadastradas no app e priorize as que vencem primeiro para reduzir custos extras."),
            DicaFinanceira(6, "Revise assinaturas", "Organização", "Confira mensalmente serviços de streaming, aplicativos e planos. Cancele ou pause o que não está sendo usado para abrir espaço no orçamento."),
            DicaFinanceira(7, "Evite parcelar sem planejamento", "Dívidas", "Antes de parcelar, verifique se as próximas prestações cabem no seu limite. Muitas parcelas pequenas podem comprometer os meses seguintes."),
            DicaFinanceira(8, "Use metas pequenas", "Planejamento", "Transforme uma meta grande em etapas mensais. Acompanhar avanços menores torna mais fácil manter o hábito de economizar.")
        )
    }
}
