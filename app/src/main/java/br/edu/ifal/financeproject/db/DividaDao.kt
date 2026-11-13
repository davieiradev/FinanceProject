package br.edu.ifal.financeproject.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.edu.ifal.financeproject.domain.Divida
import kotlinx.coroutines.flow.Flow

@Dao
interface DividaDao {

    @Insert
    suspend fun inserir(divida: Divida)

    @Delete
    suspend fun deletar(divida: Divida)

    @Query("SELECT * FROM tabela_dividas ORDER BY id DESC")
    fun listarTodas(): Flow<List<Divida>>
}