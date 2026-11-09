package br.edu.ifal.financeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import br.edu.ifal.financeproject.ui.theme.FinanceProjectTheme
import kotlinx.coroutines.launch
enum class Aba(val titulo: String, val icone: ImageVector) {
    RESUMO("Resumo", Icons.Filled.Home),
    DESPESAS("Despesas", Icons.Filled.List),

    DIVIDAS("Dívidas", Icons.Filled.DateRange ),
    LANCAR("Lançar", Icons.Filled.Add)
}

enum class TelaDivida {
    ACOMPANHAMENTO,
    CADASTRO
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val despesaDao = AppDataBase.getDatabase(applicationContext).despesaDao()
        val dividaViewModel = ViewModelProvider(this)[DividaViewModel::class.java]
        val preferencias = LimitePreferencias(applicationContext)

        setContent {
            FinanceProjectTheme {
                FluxoFinanceApp(despesaDao, dividaViewModel, preferencias)
            }
        }
    }
}

@Composable
fun FluxoFinanceApp(
    despesaDao: DespesaDao,
    dividaViewModel: DividaViewModel,
    preferencias: LimitePreferencias
) {
    var limite by remember { mutableStateOf(preferencias.ler()) }
    var abaAtual by rememberSaveable { mutableStateOf(Aba.RESUMO) }
    var telaDivida by rememberSaveable { mutableStateOf(TelaDivida.ACOMPANHAMENTO) }
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val fluxoDespesas = remember(despesaDao) { despesaDao.listarTodas() }
    val despesas by fluxoDespesas.collectAsState(initial = emptyList())

    val dividas by dividaViewModel.dividas.collectAsState()
    val estadoSelic by dividaViewModel.estadoSelic.collectAsState()
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
                telaAtual = telaDivida,
                estadoSelic = estadoSelic,
                onCadastrar = { telaDivida = TelaDivida.CADASTRO },
                onVoltarParaAcompanhamento = { telaDivida = TelaDivida.ACOMPANHAMENTO },
                onSalvar = { novaDivida ->
                    dividaViewModel.salvar(novaDivida)
                    scope.launch { snackbarState.showSnackbar("Dívida registrada!") }
                    telaDivida = TelaDivida.ACOMPANHAMENTO
                },
                onAtualizar = { divida ->
                    dividaViewModel.atualizar(divida)
                    scope.launch { snackbarState.showSnackbar("Dívida marcada como paga!") }
                },
                onExcluir = { divida ->
                    dividaViewModel.excluir(divida)
                    scope.launch { snackbarState.showSnackbar("Dívida apagada!") }
                },
                onAtualizarJuros = dividaViewModel::atualizarJurosComSelic,
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
