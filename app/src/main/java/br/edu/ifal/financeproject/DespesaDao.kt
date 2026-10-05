package br.edu.ifal.financeproject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DespesaDao {

    @Insert
    suspend fun inserir(despesa: Despesa)

    @Delete
    suspend fun deletar(despesa: Despesa)

    @Query("SELECT * FROM tabela_despesas")
    fun listarTodas(): List<Despesa>
}