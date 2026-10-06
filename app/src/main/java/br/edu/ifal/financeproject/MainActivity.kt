package br.edu.ifal.financeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import br.edu.ifal.financeproject.ui.theme.FinanceProjectTheme
import kotlinx.coroutines.launch

// Abas do app (navegação simples por estado, sem dependências extras)
enum class Aba(val titulo: String, val icone: ImageVector) {
    RESUMO("Resumo", Icons.Filled.Home),
    DESPESAS("Despesas", Icons.Filled.List),
    LANCAR("Lançar", Icons.Filled.Add)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = AppDataBase.getDatabase(applicationContext).despesaDao()

        setContent {
            FinanceProjectTheme {
                FluxoFinanceApp(dao)
            }
        }
    }
}

@Composable
fun FluxoFinanceApp(dao: DespesaDao) {
    var abaAtual by rememberSaveable { mutableStateOf(Aba.RESUMO) }
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Fonte única de dados: o banco. Resumo e Lista leem daqui.
    val despesas by dao.listarTodas().collectAsState(initial = emptyList())

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
            Aba.RESUMO -> TelaResumo(despesas, modifier)

            Aba.DESPESAS -> TelaDespesas(
                despesas = despesas,
                onExcluir = { despesa ->
                    scope.launch {
                        dao.deletar(despesa)
                        snackbarState.showSnackbar("Despesa excluída")
                    }
                },
                modifier = modifier
            )

            Aba.LANCAR -> TelaLancamento(
                onSalvar = { despesa ->
                    scope.launch {
                        dao.inserir(despesa)
                        snackbarState.showSnackbar("Despesa salva!")
                    }
                    abaAtual = Aba.DESPESAS
                },
                modifier = modifier
            )
        }
    }
}