package br.edu.ifal.financeproject

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlin.math.abs
import kotlin.math.roundToLong

@Dao
abstract class PagamentoDividaDao {


    @Query("SELECT * FROM tabela_pagamentos ORDER BY id DESC")
    abstract fun listarTodos(): Flow<List<PagamentoDivida>>

    @Query("SELECT * FROM tabela_dividas WHERE id = :id LIMIT 1")
    protected abstract suspend fun buscarDivida(id: Int): Divida?

    @Query("SELECT COALESCE(SUM(valor), 0.0) FROM tabela_pagamentos WHERE dividaId = :id")
    protected abstract suspend fun somarPagamentos(id: Int): Double

    @Insert
    protected abstract suspend fun inserirPagamento(pagamento: PagamentoDivida)

    @Query("UPDATE tabela_dividas SET paga = :quitada WHERE id = :id")
    protected abstract suspend fun atualizarQuitacao(id: Int, quitada: Boolean)

    @Transaction
    open suspend fun registrarPagamento(
        dividaId: Int,
        valor: Double,
        dataPagamento: String
    ) {
        require(valor.isFinite() && valor > 0.0) {
            "Informe um pagamento maior que zero."
        }
        require(dataPagamento.isNotBlank()) {
            "A data do pagamento é obrigatória."
        }

        val centavos = (valor * 100.0).roundToLong()
        require(centavos > 0L && abs(valor * 100.0 - centavos) < 0.00001) {
            "Informe um valor com até duas casas decimais."
        }

        val divida = buscarDivida(dividaId)
            ?: throw IllegalArgumentException("Dívida não encontrada.")

        val pagoEmCentavos = (somarPagamentos(dividaId) * 100.0).roundToLong()
        val totalEmCentavos = (divida.valor * 100.0).roundToLong()
        val restanteEmCentavos = totalEmCentavos - pagoEmCentavos

        require(!divida.paga && restanteEmCentavos > 0L) {
            "Esta dívida já está quitada."
        }
        require(centavos <= restanteEmCentavos) {
            "O pagamento não pode ultrapassar o valor restante."
        }

        inserirPagamento(
            PagamentoDivida(
                dividaId = dividaId,
                valor = centavos / 100.0,
                dataPagamento = dataPagamento
            )
        )

        if (centavos == restanteEmCentavos) {
            atualizarQuitacao(dividaId, true)
        }
    }
}
