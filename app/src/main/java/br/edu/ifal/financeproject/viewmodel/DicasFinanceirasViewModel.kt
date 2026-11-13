package br.edu.ifal.financeproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifal.financeproject.db.DicaFinanceiraDao
import br.edu.ifal.financeproject.domain.DicaFinanceira
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DicasFinanceirasViewModel(
    private val dao: DicaFinanceiraDao
) : ViewModel() {

    val dicas = dao.listarTodas().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            val dicasIniciais = listOf(
                DicaFinanceira(
                    titulo = "Anote seus gastos",
                    categoria = "Organização",
                    descricao = "Registrar o que você gasta ajuda a entender para onde o dinheiro está indo."
                ),
                DicaFinanceira(
                    titulo = "Separe necessidades e desejos",
                    categoria = "Planejamento",
                    descricao = "Antes de uma compra, pense se ela é necessária agora ou se pode esperar."
                ),
                DicaFinanceira(
                    titulo = "Monte uma reserva aos poucos",
                    categoria = "Reserva financeira",
                    descricao = "Guardar um valor pequeno com frequência já é um começo para imprevistos."
                ),
                DicaFinanceira(
                    titulo = "Compare antes de comprar",
                    categoria = "Consumo consciente",
                    descricao = "Compare preços e condições para escolher com calma e evitar gastos por impulso."
                ),
                DicaFinanceira(
                    titulo = "Revise suas assinaturas",
                    categoria = "Organização",
                    descricao = "Confira serviços recorrentes e mantenha apenas aqueles que você realmente usa."
                ),
                DicaFinanceira(
                    titulo = "Defina prioridades para o mês",
                    categoria = "Planejamento",
                    descricao = "Liste primeiro as contas essenciais e compromissos com data certa para organizar o restante do orçamento."
                ),
                DicaFinanceira(
                    titulo = "Crie uma meta possível",
                    categoria = "Planejamento",
                    descricao = "Escolha um objetivo específico e um valor que caiba na sua realidade. Metas menores ajudam a acompanhar o progresso."
                ),
                DicaFinanceira(
                    titulo = "Planeje as compras de mercado",
                    categoria = "Consumo consciente",
                    descricao = "Fazer uma lista antes de sair ajuda a evitar compras por impulso e desperdício de alimentos."
                ),
                DicaFinanceira(
                    titulo = "Espere antes de uma compra não planejada",
                    categoria = "Consumo consciente",
                    descricao = "Dar um tempo para pensar ajuda a decidir se a compra ainda faz sentido depois da empolgação inicial."
                ),
                DicaFinanceira(
                    titulo = "Conheça o custo total de uma dívida",
                    categoria = "Dívidas",
                    descricao = "Compare juros, tarifas e valor total a pagar. A parcela menor nem sempre representa o menor custo."
                ),
                DicaFinanceira(
                    titulo = "Priorize dívidas com juros altos",
                    categoria = "Dívidas",
                    descricao = "Entender quais dívidas crescem mais rápido pode ajudar a planejar por onde começar a negociação."
                ),
                DicaFinanceira(
                    titulo = "Leia as condições antes de contratar",
                    categoria = "Segurança financeira",
                    descricao = "Confira taxas, prazos, multas e regras de cancelamento antes de aceitar um serviço ou crédito."
                ),
                DicaFinanceira(
                    titulo = "Desconfie de promessas de dinheiro fácil",
                    categoria = "Segurança financeira",
                    descricao = "Pesquise a empresa e nunca compartilhe senhas ou códigos recebidos por mensagem."
                ),
                DicaFinanceira(
                    titulo = "Revise seu orçamento com frequência",
                    categoria = "Organização",
                    descricao = "Uma revisão semanal ou mensal permite perceber mudanças e ajustar o planejamento com antecedência."
                ),
                DicaFinanceira(
                    titulo = "Comemore pequenos avanços",
                    categoria = "Bem-estar financeiro",
                    descricao = "Manter um hábito financeiro é um processo. Reconheça o que está funcionando e ajuste o que não couber na sua rotina."
                )
            )

            val titulosSalvos = dao.listarTitulosSalvos().toSet()
            val dicasNovas = dicasIniciais.filterNot { it.titulo in titulosSalvos }
            if (dicasNovas.isNotEmpty()) {
                dao.inserirTodas(dicasNovas)
            }
        }
    }
}

class DicasFinanceirasViewModelFactory(
    private val dao: DicaFinanceiraDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DicasFinanceirasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DicasFinanceirasViewModel(dao) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido")
    }
}
