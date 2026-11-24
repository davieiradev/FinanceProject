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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
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
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import br.edu.ifal.financeproject.ui.theme.FinanceProjectTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

enum class Aba(val titulo: String, val icone: ImageVector) {
    RESUMO("Resumo", Icons.Filled.Home),
    DESPESAS("Despesas", Icons.Filled.List),
    DIVIDAS("Dívidas", Icons.Filled.DateRange),
    LANCAR("Lançar", Icons.Filled.Add)
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
        val despesaDao = banco.despesaDao()
        val dividaDao = banco.dividaDao()
        val pagamentoDao = banco.pagamentoDividaDao()
        val preferencias = LimitePreferencias(applicationContext)

        setContent {
            FinanceProjectTheme {
                FluxoFinanceApp(
                    despesaDao,
                    dividaDao,
                    pagamentoDao,
                    preferencias,
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
    despesaDao: DespesaDao,
    dividaDao: DividaDao,
    pagamentoDao: PagamentoDividaDao,
    preferencias: LimitePreferencias,
    notificacoesPermitidas: Boolean,
    onSolicitarPermissaoDeNotificacao: () -> Unit
) {
    var limite by remember { mutableStateOf(preferencias.ler()) }
    var abaAtual by rememberSaveable { mutableStateOf(Aba.RESUMO) }
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val fluxoDespesas = remember(despesaDao) { despesaDao.listarTodas() }
    val despesas by fluxoDespesas.collectAsState(initial = emptyList())

    val fluxoDividas = remember(dividaDao) { dividaDao.listarTodas() }
    val dividas by fluxoDividas.collectAsState(initial = emptyList())

    val fluxoPagamentos = remember(pagamentoDao) { pagamentoDao.listarTodos() }
    val pagamentos by fluxoPagamentos.collectAsState(initial = emptyList())
    val context = LocalContext.current

    LaunchedEffect(despesas, dividas, notificacoesPermitidas) {
        if (notificacoesPermitidas) {
            enviarAlertaAcolhedor(context, alertasRecorrencia(despesas, dividas = dividas))
        }
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
                despesas = despesas,
                dividas = dividas,
                limite = limite,
                notificacoesPermitidas = notificacoesPermitidas,
                onSolicitarPermissaoDeNotificacao = onSolicitarPermissaoDeNotificacao,
                onSalvarLimite = { valor ->
                    preferencias.salvar(valor)
                    limite = valor
                },
                modifier = modifier
            )

            Aba.DESPESAS -> TelaDespesas(
                despesas = despesas,
                onExcluir = { despesa ->
                    scope.launch {
                        despesaDao.deletar(despesa)
                        snackbarState.showSnackbar("Despesa excluída")
                    }
                },
                modifier = modifier
            )

            Aba.DIVIDAS -> TelaDividas(
                dividas = dividas,
                pagamentos = pagamentos,
                onSalvar = { novaDivida ->
                    scope.launch {
                        dividaDao.inserir(novaDivida)
                        snackbarState.showSnackbar("Dívida registrada!")
                    }
                },
                onExcluir = { divida ->
                    scope.launch {
                        dividaDao.deletar(divida)
                        snackbarState.showSnackbar("Dívida apagada!")
                    }
                },
                onRegistrarPagamento = { dividaId, valorPago ->
                    scope.launch {
                        try {
                            val hoje = SimpleDateFormat(
                                "dd/MM/yyyy", Locale("pt", "BR")
                            ).format(Date())
                            pagamentoDao.registrarPagamento(dividaId, valorPago, hoje)
                            snackbarState.showSnackbar("Pagamento registrado!")
                        } catch (e: Exception) {
                            snackbarState.showSnackbar(
                                "Não foi possível registrar: ${e.message ?: "erro desconhecido"}"
                            )
                        }
                    }
                },
                modifier = modifier
            )

            Aba.LANCAR -> TelaLancamento(
                categoriasMaisUsadas = categoriasFrequentes(despesas.map { it.categoria }),
                onSalvar = { despesa ->
                    scope.launch {
                        despesaDao.inserir(despesa)
                        snackbarState.showSnackbar("Despesa salva!")
                    }
                    abaAtual = Aba.DESPESAS
                },
                modifier = modifier
            )
        }
    }
}
