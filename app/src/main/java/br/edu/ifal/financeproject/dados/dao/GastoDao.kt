package br.edu.ifal.financeproject.dados.dao

import androidx.room.*
import br.edu.ifal.financeproject.dados.modelo.FormaPagamento
import br.edu.ifal.financeproject.dados.modelo.Gasto
import br.edu.ifal.financeproject.dados.modelo.GastoComParcelas
import br.edu.ifal.financeproject.dados.modelo.Parcela
import br.edu.ifal.financeproject.financas.gerarParcelas
import br.edu.ifal.financeproject.financas.interpretarData
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GastoDao {
    @Transaction
    @Query("SELECT * FROM gastos ORDER BY id DESC")
    abstract fun observar(): Flow<List<GastoComParcelas>>

    @Insert
    protected abstract suspend fun inserir(gasto: Gasto): Long

    @Insert
    protected abstract suspend fun inserirParcelas(parcelas: List<Parcela>)

    @Transaction
    open suspend fun registrar(gasto: Gasto, quantidade: Int, primeiroVencimento: String?) {
        require(gasto.totalCentavos > 0 && gasto.titulo.isNotBlank() && interpretarData(gasto.data) != null)
        val id = inserir(gasto)
        when (gasto.forma) {
            FormaPagamento.A_VISTA -> require(quantidade == 0 && primeiroVencimento == null)
            FormaPagamento.A_PRAZO -> inserirParcelas(gerarParcelas(id, gasto.totalCentavos, quantidade,
                requireNotNull(primeiroVencimento)))
        }
    }

    @Delete
    abstract suspend fun excluir(gasto: Gasto)

    @Query("UPDATE parcelas SET dataPagamento = :data WHERE gastoId = :id AND numero = :numero AND dataPagamento IS NULL")
    abstract suspend fun pagar(id: Long, numero: Int, data: String): Int

    @Query("UPDATE parcelas SET lembreteEnviado = 1 WHERE gastoId = :id AND numero = :numero AND dataPagamento IS NULL AND lembreteEnviado = 0")
    abstract suspend fun marcarLembrete(id: Long, numero: Int): Int
}
