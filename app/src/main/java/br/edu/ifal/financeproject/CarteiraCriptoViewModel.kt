package br.edu.ifal.financeproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifal.financeproject.CarteiraCripto
import br.edu.ifal.financeproject.CarteiraCriptoDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarteiraCriptoViewModel(
    private val dao: CarteiraCriptoDao
) : ViewModel() {

    val carteira = dao.listarCarteira().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            if (dao.contarCriptomoedas() == 0) {
                dao.inserirTodas(
                    listOf(
                        CarteiraCripto(
                            simbolo = "BTC",
                            nome = "Bitcoin",
                            quantidade = 0.002,
                            precoMedioCompra = 60000.0,
                            cotacaoReferencia = 65000.0
                        ),
                        CarteiraCripto(
                            simbolo = "ETH",
                            nome = "Ethereum",
                            quantidade = 0.05,
                            precoMedioCompra = 3000.0,
                            cotacaoReferencia = 3200.0
                        )
                    )
                )
            }
        }
    }
}

class CarteiraCriptoViewModelFactory(
    private val dao: CarteiraCriptoDao
) : androidx.lifecycle.ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarteiraCriptoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarteiraCriptoViewModel(dao) as T
        }

        throw IllegalArgumentException("ViewModel desconhecido")
    }
}