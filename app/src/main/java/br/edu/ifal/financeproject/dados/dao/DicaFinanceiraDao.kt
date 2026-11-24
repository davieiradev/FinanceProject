package br.edu.ifal.financeproject.dados.dao

import androidx.room.Dao
import androidx.room.Query
import br.edu.ifal.financeproject.dados.modelo.DicaFinanceira
import kotlinx.coroutines.flow.Flow

@Dao
interface DicaFinanceiraDao {
    @Query("SELECT * FROM dicas_financeiras ORDER BY categoria ASC, titulo ASC")
    fun observarTodas(): Flow<List<DicaFinanceira>>
}
