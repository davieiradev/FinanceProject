package br.edu.ifal.financeproject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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
