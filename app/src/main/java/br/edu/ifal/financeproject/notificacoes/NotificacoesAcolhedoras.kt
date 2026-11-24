package br.edu.ifal.financeproject.notificacoes

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.room.withTransaction
import br.edu.ifal.financeproject.R
import br.edu.ifal.financeproject.dados.AppDataBase
import br.edu.ifal.financeproject.dados.modelo.GastoComParcelas
import br.edu.ifal.financeproject.dados.modelo.Parcela
import br.edu.ifal.financeproject.financas.AlertaRecorrencia
import br.edu.ifal.financeproject.financas.alertasRecorrencia
import br.edu.ifal.financeproject.financas.diasAteLembrete

private const val CANAL_ALERTAS = "alertas_acolhedores"
private const val ID_ALERTA_RECORRENTE = 14

fun configurarCanalDeAlertas(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val canal = NotificationChannel(
        CANAL_ALERTAS,
        "Lembretes acolhedores",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Lembretes para revisar gastos e parcelas com calma."
    }
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
}

fun notificacoesPermitidas(context: Context): Boolean =
    NotificationManagerCompat.from(context).areNotificationsEnabled() &&
        (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED)


fun enviarAlertaAcolhedor(context: Context, alertas: List<AlertaRecorrencia>) {
    if (alertas.isEmpty() || !notificacoesPermitidas(context)) return

    configurarCanalDeAlertas(context)

    val resumo = alertas.joinToString(separator = " | ") {
        "${it.categoria}: ${it.quantidade} registros"
    }

    val mensagem = if (alertas.size == 1) {
        "Percebemos ${alertas.first().quantidade} registros em ${alertas.first().categoria}. Que tal revisar com calma?"
    } else {
        "Algumas categorias aparecem com frequência. Que tal olhar seus registros com calma?"
    }

    val notificacao = NotificationCompat.Builder(context, CANAL_ALERTAS)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Um lembrete para você")
        .setContentText(mensagem)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("$mensagem\n$resumo")
        )
        .setOnlyAlertOnce(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context)
        .notify(ID_ALERTA_RECORRENTE, notificacao)
}

private const val ID_PARCELA = 19
private fun etiqueta(parcela: Parcela) = "parcela-${parcela.gastoId}-${parcela.numero}"

fun cancelarLembrete(context: Context, parcela: Parcela) {
    NotificationManagerCompat.from(context).cancel(etiqueta(parcela), ID_PARCELA)
}

suspend fun verificarAlertas(context: Context, registros: List<GastoComParcelas>) {
    if (!notificacoesPermitidas(context)) return
    configurarCanalDeAlertas(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        context.getSystemService(NotificationManager::class.java)
            .getNotificationChannel(CANAL_ALERTAS).importance == NotificationManager.IMPORTANCE_NONE) return

    val banco = AppDataBase.getDatabase(context)
    try {
        enviarAlertaAcolhedor(context, alertasRecorrencia(registros.map { it.gasto }))
        for ((gasto, parcelas) in registros) {
            for (parcela in parcelas) {
                if (parcela.dataPagamento != null || parcela.lembreteEnviado) continue
                val dias = diasAteLembrete(parcela.vencimento) ?: continue
                val quando = when (dias) {
                    0 -> "hoje"
                    1 -> "amanhã"
                    else -> "em $dias dias"
                }
                val mensagem = "Oi! Sua parcela ${parcela.numero} de ${gasto.titulo} vence $quando. " +
                    "Quando puder, é só registrar o pagamento."
                val aviso = NotificationCompat.Builder(context, CANAL_ALERTAS)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Lembrete de parcela")
                    .setContentText(mensagem)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(mensagem))
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .build()
                banco.withTransaction {
                    if (banco.gastoDao().marcarLembrete(gasto.id, parcela.numero) == 1) {
                        NotificationManagerCompat.from(context).notify(etiqueta(parcela), ID_PARCELA, aviso)
                    }
                }
            }
        }
    } catch (_: SecurityException) {
        // A permissão pode mudar enquanto o lembrete está sendo enviado.
    }
}
