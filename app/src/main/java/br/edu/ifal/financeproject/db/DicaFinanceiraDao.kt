package br.edu.ifal.financeproject.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.edu.ifal.financeproject.domain.DicaFinanceira
import kotlinx.coroutines.flow.Flow

@Dao
interface DicaFinanceiraDao {
    @Query("SELECT * FROM tabela_dicas_financeiras ORDER BY id ASC")
    fun listarTodas(): Flow<List<DicaFinanceira>>

    @Query("SELECT titulo FROM tabela_dicas_financeiras")
    suspend fun listarTitulosSalvos(): List<String>

    @Insert
    suspend fun inserirTodas(dicas: List<DicaFinanceira>)
}
