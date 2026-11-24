package br.edu.ifal.financeproject.dados.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.edu.ifal.financeproject.dados.modelo.ProgressoLicao
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressoLicaoDao {
    @Query("SELECT * FROM progresso_licoes ORDER BY licaoId ASC")
    fun observarProgresso(): Flow<List<ProgressoLicao>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun marcarConcluida(progresso: ProgressoLicao)
}
