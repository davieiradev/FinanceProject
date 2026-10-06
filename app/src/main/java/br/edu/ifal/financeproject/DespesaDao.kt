package br.edu.ifal.financeproject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DespesaDao {

    @Insert
    suspend fun inserir(despesa: Despesa)

    @Delete
    suspend fun deletar(despesa: Despesa)

    @Query("SELECT * FROM tabela_despesas ORDER BY id DESC")
    fun listarTodas(): Flow<List<Despesa>>
}