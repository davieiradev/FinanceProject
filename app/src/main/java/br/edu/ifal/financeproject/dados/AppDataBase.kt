package br.edu.ifal.financeproject.dados

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.edu.ifal.financeproject.dados.dao.GastoDao
import br.edu.ifal.financeproject.dados.dao.ProgressoLicaoDao
import br.edu.ifal.financeproject.dados.modelo.Gasto
import br.edu.ifal.financeproject.dados.modelo.Parcela
import br.edu.ifal.financeproject.dados.modelo.ProgressoLicao

@Database(entities = [Gasto::class, Parcela::class, ProgressoLicao::class], version = 9, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun gastoDao(): GastoDao
    abstract fun progressoLicaoDao(): ProgressoLicaoDao

    companion object {
        @Volatile private var instance: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, "banco_despesas")
                .addMigrations(object : androidx.room.migration.Migration(8, 9) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL("ALTER TABLE gastos ADD COLUMN jurosCentavos INTEGER NOT NULL DEFAULT 0")
                    }
                })
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onDestructiveMigration(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        // O Room 2.6 remove apenas tabelas conhecidas pelo novo modelo.
                        db.execSQL("DROP TABLE IF EXISTS tabela_pagamentos")
                        db.execSQL("DROP TABLE IF EXISTS tabela_dividas")
                        db.execSQL("DROP TABLE IF EXISTS tabela_despesas")
                    }
                })
                .build().also { instance = it }
        }
    }
}
