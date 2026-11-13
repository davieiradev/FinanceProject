package br.edu.ifal.financeproject.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import br.edu.ifal.financeproject.domain.CarteiraCripto

@Dao
interface CarteiraCriptoDao {

    @Query("SELECT * FROM tabela_carteira_cripto ORDER BY nome ASC")
    fun listarCarteira(): Flow<List<CarteiraCripto>>

    @Query("SELECT COUNT(*) FROM tabela_carteira_cripto")
    suspend fun contarCriptomoedas(): Int

    @Insert
    suspend fun inserirTodas(criptomoedas: List<CarteiraCripto>)
}