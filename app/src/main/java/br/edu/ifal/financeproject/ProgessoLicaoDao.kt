package br.edu.ifal.financeproject

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressoLicaoDao {
    @Query("SELECT * FROM progresso_licoes ORDER BY licaoId ASC")
    fun observarProgresso(): Flow<List<ProgressoLicao>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun marcarConcluida(progresso: ProgressoLicao)
}
