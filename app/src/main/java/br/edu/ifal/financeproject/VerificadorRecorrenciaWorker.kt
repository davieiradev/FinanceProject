package br.edu.ifal.financeproject

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first

class VerificadorRecorrenciaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val banco = AppDataBase.getDatabase(applicationContext)

            val despesas = banco.despesaDao()
                .listarTodas()
                .first()

            val dividas = banco.dividaDao()
                .listarTodas()
                .first()

            val alertas = alertasRecorrencia(
                despesas = despesas,
                dividas = dividas
            )

            configurarCanalDeAlertas(applicationContext)

            enviarAlertaAcolhedor(
                applicationContext,
                alertas
            )

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}
