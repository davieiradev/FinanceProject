package br.edu.ifal.financeproject

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

private const val CANAL_ALERTAS = "alertas_acolhedores"
private const val ID_ALERTA_RECORRENTE = 14
private const val PREFERENCIAS_NOTIFICACOES = "notificacoes_fluxo_finance"
private const val CHAVE_ULTIMO_ALERTA = "ultimo_alerta_enviado"

fun configurarCanalDeAlertas(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val canal = NotificationChannel(
        CANAL_ALERTAS,
        "Lembretes acolhedores",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Lembretes para revisar gastos e dívidas com calma."
    }
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
}

fun notificacoesPermitidas(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED


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
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context)
        .notify(ID_ALERTA_RECORRENTE, notificacao)
}
