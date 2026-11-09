package br.edu.ifal.financeproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface EstadoSelic {
    data object Inicial : EstadoSelic
    data object Carregando : EstadoSelic
    data class Sucesso(val dividasAtualizadas: Map<Int, DividaAtualizada>) : EstadoSelic
    data class Erro(val mensagem: String) : EstadoSelic
}

class DividaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDataBase.getDatabase(application).dividaDao()
    private val repositorioSelic = RepositorioSelic()

    val dividas: StateFlow<List<Divida>> = dao.listarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _estadoSelic = MutableStateFlow<EstadoSelic>(EstadoSelic.Inicial)
    val estadoSelic = _estadoSelic.asStateFlow()

    fun salvar(divida: Divida) = alterarBanco { dao.inserir(divida) }

    fun atualizar(divida: Divida) = alterarBanco { dao.atualizar(divida) }

    fun excluir(divida: Divida) = alterarBanco { dao.deletar(divida) }

    private fun alterarBanco(operacao: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            operacao()
            _estadoSelic.value = EstadoSelic.Inicial
        }
    }

    fun atualizarJurosComSelic() {
        val pendentes = dividas.value.filterNot { it.paga }
        if (pendentes.none { estaEmAtraso(it) }) {
            _estadoSelic.value = EstadoSelic.Erro("Não há dívidas em atraso para calcular juros.")
            return
        }
        viewModelScope.launch {
            _estadoSelic.value = EstadoSelic.Carregando
            _estadoSelic.value = try {
                val resultado = withContext(Dispatchers.IO) {
                    repositorioSelic.calcularDividasEmAtraso(pendentes)
                }
                EstadoSelic.Sucesso(resultado)
            } catch (_: Exception) {
                EstadoSelic.Erro("Não foi possível consultar a Selic agora. Verifique sua internet e tente novamente.")
            }
        }
    }
}
