package br.edu.ifal.financeproject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.edu.ifal.financeproject.model.Despesa
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