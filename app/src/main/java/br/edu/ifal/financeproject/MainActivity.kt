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
import br.edu.ifal.financeproject.ui.theme.FinanceProjectTheme
import kotlinx.coroutines.launch
enum class Aba(val titulo: String, val icone: ImageVector) {
    RESUMO("Resumo", Icons.Filled.Home),
    DESPESAS("Despesas", Icons.Filled.List),

    DIVIDAS("Dívidas", Icons.Filled.DateRange ),
    LANCAR("Lançar", Icons.Filled.Add)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val despesaDao = AppDataBase.getDatabase(applicationContext).despesaDao()
        val dividaDao = AppDataBase.getDatabase(applicationContext).dividaDao()
        val preferencias = LimitePreferencias(applicationContext)

        setContent {
            FinanceProjectTheme {
                FluxoFinanceApp(despesaDao, dividaDao, preferencias)
            }
        }
    }
}

@Composable
fun FluxoFinanceApp(despesaDao: DespesaDao, dividaDao: DividaDao, preferencias: LimitePreferencias) {
    var limite by remember { mutableStateOf(preferencias.ler()) }
    var abaAtual by rememberSaveable { mutableStateOf(Aba.RESUMO) }
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val fluxoDespesas = remember(despesaDao) { despesaDao.listarTodas() }
    val despesas by fluxoDespesas.collectAsState(initial = emptyList())

    val fluxoDividas = remember(dividaDao){dividaDao.listarTodas()}
    val dividas by fluxoDividas.collectAsState(initial = emptyList())
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
                onSalvar = { novaDivida -> scope.launch {
                    dividaDao.inserir(novaDivida)
                    snackbarState.showSnackbar(("Dívida registrada!"))
                }
                },
                onExcluir = {divida ->
                    scope.launch{
                        dividaDao.deletar(divida)
                        snackbarState.showSnackbar("Dívida apagada!")
                    }
                },
                modifier = modifier
            )

            Aba.LANCAR -> TelaLancamento(
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
