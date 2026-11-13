package br.edu.ifal.financeproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifal.financeproject.domain.DadoEconomico
import br.edu.ifal.financeproject.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EstadoInflacaoIpca(
    val resultados: List<DadoEconomico> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

class InflacaoIpcaViewModel : ViewModel() {
    private val _estado = MutableStateFlow(EstadoInflacaoIpca(carregando = true))
    val estado: StateFlow<EstadoInflacaoIpca> = _estado

    init {
        atualizar()
    }

    fun atualizar() {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(carregando = true, erro = null)
            try {
                val resultados = RetrofitClient.service.getUltimosIpca()
                _estado.value = if (resultados.isEmpty()) {
                    EstadoInflacaoIpca(
                        erro = "O Banco Central não retornou resultados do IPCA. Tente novamente."
                    )
                } else {
                    EstadoInflacaoIpca(resultados = resultados)
                }
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    carregando = false,
                    erro = "Não foi possível carregar o IPCA. Confira sua conexão e tente novamente."
                )
            }
        }
    }
}
