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
import androidx.compose.material.icons.filled.Info
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
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifal.financeproject.MercadoCriptoViewModel
import br.edu.ifal.financeproject.TelaMercadoCripto
import br.edu.ifal.financeproject.CarteiraCriptoViewModel
import br.edu.ifal.financeproject.CarteiraCriptoViewModelFactory
import br.edu.ifal.financeproject.TelaCarteiraCripto
enum class Aba(val titulo: String, val icone: ImageVector) {
    RESUMO("Resumo", Icons.Filled.Home),
    INDICADORES("Economia", Icons.Filled.Info),
    MERCADO_CRIPTO("Mercado", Icons.Filled.List),
    CARTEIRA_CRIPTO("Carteira", Icons.Filled.Add)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDataBase.getDatabase(applicationContext)
        val despesaDao = database.despesaDao()
        val dividaDao = database.dividaDao()
        val carteiraCriptoDao = database.carteiraCriptoDao()
        val preferencias = LimitePreferencias(applicationContext)

        setContent {
            FinanceProjectTheme {
                FluxoFinanceApp(
                    despesaDao,
                    dividaDao,
                    preferencias,
                    carteiraCriptoDao
                )
            }
        }
    }
}

@Composable
fun FluxoFinanceApp(despesaDao: DespesaDao, dividaDao: DividaDao, preferencias: LimitePreferencias,  carteiraCriptoDao: CarteiraCriptoDao) {
    var limite by remember { mutableStateOf(preferencias.ler()) }
    var abaAtual by rememberSaveable { mutableStateOf(Aba.RESUMO) }
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val mercadoViewModel: MercadoCriptoViewModel = viewModel()

    val carteiraViewModel: CarteiraCriptoViewModel = viewModel(
        factory = CarteiraCriptoViewModelFactory(carteiraCriptoDao)
    )

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
                dividas = dividas,
                limite = limite,
                onSalvarLimite = { valor ->
                    preferencias.salvar(valor)
                    limite = valor
                },
                modifier = modifier
            )
            Aba.INDICADORES -> TelaIndicadores(modifier = modifier)
            Aba.MERCADO_CRIPTO -> TelaMercadoCripto(
                viewModel = mercadoViewModel
            )

            Aba.CARTEIRA_CRIPTO -> TelaCarteiraCripto(
                viewModel = carteiraViewModel
            )
        }
    }
}
