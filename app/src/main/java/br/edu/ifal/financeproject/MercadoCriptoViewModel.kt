package br.edu.ifal.financeproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifal.financeproject.CotacaoCripto
import br.edu.ifal.financeproject.CryptoRetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MercadoCriptoViewModel : ViewModel() {

    private val _cotacoes = MutableStateFlow<List<CotacaoCripto>>(emptyList())
    val cotacoes: StateFlow<List<CotacaoCripto>> = _cotacoes

    private val _carregando = MutableStateFlow(true)
    val carregando: StateFlow<Boolean> = _carregando

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro

    private val moedas = listOf("BTCUSDT", "ETHUSDT", "SOLUSDT")

    init {
        atualizarPeriodicamente()
    }

    private fun atualizarPeriodicamente() {
        viewModelScope.launch {
            while (true) {
                buscarCotacoes()
                delay(30_000)
            }
        }
    }

    private suspend fun buscarCotacoes() {
        try {
            val resultado = moedas.map {
                CryptoRetrofitClient.api.buscarCotacao(it)
            }

            _cotacoes.value = resultado
            _erro.value = null
        } catch (e: Exception) {
            _erro.value = "Não foi possível carregar as cotações."
        } finally {
            _carregando.value = false
        }
    }
}