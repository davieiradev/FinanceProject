package br.edu.ifal.financeproject

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.edu.ifal.financeproject.dados.AppDataBase
import br.edu.ifal.financeproject.notificacoes.verificarAlertas
import kotlinx.coroutines.flow.first

class VerificadorRecorrenciaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val banco = AppDataBase.getDatabase(applicationContext)

            verificarAlertas(applicationContext, banco.gastoDao().observar().first())
            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}
