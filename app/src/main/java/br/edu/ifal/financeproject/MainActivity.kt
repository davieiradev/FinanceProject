package br.edu.ifal.financeproject

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import br.edu.ifal.financeproject.dados.AppDataBase
import br.edu.ifal.financeproject.dados.LimitePreferencias
import br.edu.ifal.financeproject.dados.dao.GastoDao
import br.edu.ifal.financeproject.dados.dao.ProgressoLicaoDao
import br.edu.ifal.financeproject.dados.modelo.ProgressoLicao
import br.edu.ifal.financeproject.financas.categoriasFrequentes
import br.edu.ifal.financeproject.financas.dataHoje
import br.edu.ifal.financeproject.notificacoes.cancelarLembrete
import br.edu.ifal.financeproject.notificacoes.configurarCanalDeAlertas
import br.edu.ifal.financeproject.notificacoes.notificacoesPermitidas
import br.edu.ifal.financeproject.notificacoes.verificarAlertas
import br.edu.ifal.financeproject.ui.telas.TelaEducacaoFinanceira
import br.edu.ifal.financeproject.ui.telas.TelaGastos
import br.edu.ifal.financeproject.ui.telas.TelaLancamento
import br.edu.ifal.financeproject.ui.telas.TelaResumo
import br.edu.ifal.financeproject.ui.theme.FinanceProjectTheme
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch

enum class Aba(val titulo: String, val icone: ImageVector) {
    RESUMO("Resumo", Icons.Filled.Home),
    GASTOS("Gastos", Icons.Filled.List),
    LANCAR("Lançar", Icons.Filled.Add),

    APRENDER("Aprender", Icons.Filled.Info)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configurarCanalDeAlertas(applicationContext)
        val verificacaoRecorrencias =
            PeriodicWorkRequestBuilder<VerificadorRecorrenciaWorker>(
                15, TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "verificacao_recorrencias",
                ExistingPeriodicWorkPolicy.KEEP,
                verificacaoRecorrencias
            )
        var podeNotificar by mutableStateOf(notificacoesPermitidas(applicationContext))
        val pedirPermissaoNotificacoes = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedida ->
            podeNotificar = concedida
        }
        val banco = AppDataBase.getDatabase(applicationContext)
        val gastoDao = banco.gastoDao()
        val preferencias = LimitePreferencias(applicationContext)
        val progressoDao = AppDataBase.getDatabase(
            applicationContext
        ).progressoLicaoDao()

        setContent {
            FinanceProjectTheme {
                FluxoFinanceApp(
                    gastoDao,
                    preferencias,
                    progressoDao = progressoDao,
                    notificacoesPermitidas = podeNotificar,
                    onSolicitarPermissaoDeNotificacao = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            pedirPermissaoNotificacoes.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FluxoFinanceApp(
    gastoDao: GastoDao,
    preferencias: LimitePreferencias,
    notificacoesPermitidas: Boolean,
    onSolicitarPermissaoDeNotificacao: () -> Unit,
    progressoDao: ProgressoLicaoDao
) {
    var limite by remember { mutableStateOf(preferencias.ler()) }
    var abaAtual by rememberSaveable { mutableStateOf(Aba.RESUMO) }
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val fluxoGastos = remember(gastoDao) { gastoDao.observar() }
    val registros by fluxoGastos.collectAsState(initial = emptyList())
    val context = LocalContext.current

    val fluxoProgresso = remember(progressoDao) { progressoDao.observarProgresso() }
    val progresso by fluxoProgresso.collectAsState(initial = emptyList())

    LaunchedEffect(registros, notificacoesPermitidas) {
        if (notificacoesPermitidas) verificarAlertas(context, registros)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        bottomBar = {
            NavigationBar {
                Aba.entries.forEach { aba ->
                    NavigationBarItem(
                        selected = abaAtual == aba,
                        onClick = { abaAtual = aba },
                        icon = { Icon(aba.icone, contentDescription = aba.titulo) },
                        label = { Text(aba.titulo) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when (abaAtual) {
            Aba.RESUMO -> TelaResumo(
                registros = registros,
                limite = limite,
                notificacoesPermitidas = notificacoesPermitidas,
                onSolicitarPermissaoDeNotificacao = onSolicitarPermissaoDeNotificacao,
                onSalvarLimite = { valor ->
                    preferencias.salvar(valor)
                    limite = valor
                },
                modifier = modifier
            )

            Aba.GASTOS -> TelaGastos(
                registros = registros,
                onExcluir = { gasto ->
                    scope.launch {
                        gastoDao.excluir(gasto.gasto)
                        gasto.parcelas.forEach { cancelarLembrete(context, it) }
                        snackbarState.showSnackbar("Gasto excluído")
                    }
                },
                onPagar = { parcela ->
                    scope.launch {
                        if (gastoDao.pagar(parcela.gastoId, parcela.numero, dataHoje()) == 1) {
                            cancelarLembrete(context, parcela)
                            snackbarState.showSnackbar("Pagamento registrado")
                        }
                    }
                },
                modifier = modifier
            )

            Aba.LANCAR -> TelaLancamento(
                categoriasMaisUsadas = categoriasFrequentes(registros.map { it.gasto.categoria }),
                onSalvar = { gasto, quantidade, primeira ->
                    gastoDao.registrar(gasto, quantidade, primeira)
                    abaAtual = Aba.GASTOS
                    scope.launch { snackbarState.showSnackbar("Gasto salvo") }
                },
                modifier = modifier
            )

            Aba.APRENDER -> TelaEducacaoFinanceira(
                concluidas = progresso.filter { it.concluida }.map { it.licaoId }.toSet(),
                onConcluir = { id ->
                    scope.launch { progressoDao.marcarConcluida(ProgressoLicao(id)) }
                },
                modifier = modifier
            )
        }
    }
}
