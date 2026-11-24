package br.edu.ifal.financeproject.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.ui.theme.FluxoCard

private data class LicaoFinanceira(
    val id: Int,
    val titulo: String,
    val conteudo: String,
    val orientacao: String
)

// Cada conteúdo possui menos de 120 palavras.
private val licoesFinanceiras = listOf(
    LicaoFinanceira(
        1,
        "Gasto à vista e a prazo",
        "Ao pagar à vista, você usa o saldo disponível na hora. Ao comprar a prazo, " +
                "escolha o número de parcelas e o primeiro vencimento. Cada parcela reduz " +
                "o saldo quando você registra o pagamento.",
        "Abra Lançar e preencha a descrição e o valor. Digite 1250 para lançar R$ 12,50. Escolha a categoria e toque em À vista ou A prazo. Se for a prazo, preencha as parcelas e o vencimento. Toque em Guardar gasto."
    ),
    LicaoFinanceira(
        2,
        "Limite e saldo",
        "Limite é o valor máximo que você decide utilizar para seus gastos em um período. " +
                "Saldo disponível é a diferença entre esse limite e o total de gastos já pagos. " +
                "Por exemplo: com limite de R$ 1.000 e gastos de R$ 650, " +
                "sobram R$ 350 dentro do limite. Isso não significa necessariamente que " +
                "existam R$ 350 na conta bancária. Confira os valores do aplicativo e " +
                "reserve parte do orçamento para gastos essenciais e imprevistos.",
        "Em Resumo, toque em Definir limite, digite o valor e toque em Salvar. Depois de pagar uma parcela, volte ao Resumo para ver o saldo atualizado."
    ),
    LicaoFinanceira(
        3,
        "O que são juros?",
        "Juros são um valor cobrado ou recebido pelo uso do dinheiro durante certo tempo. " +
                "Nos juros simples, o cálculo considera sempre o valor inicial. Nos juros " +
                "compostos, os juros de cada período passam a integrar a base do próximo. " +
                "Por exemplo, R$ 100 com taxa de 10% ao mês viram R$ 110 no primeiro mês " +
                "e R$ 121 no segundo, sem pagamentos intermediários.",
        "Em Resumo, toque em Simular. Informe o valor inicial, a taxa mensal em % e o prazo. Toque em Simular para ver a estimativa. Ao registrar uma compra em Lançar, escolha A prazo e Com juros. Nesse campo, digite o total dos juros em reais."
    ),
    LicaoFinanceira(
        4,
        "Pagar em partes",
        "Cada parcela tem um valor e um vencimento. Em uma compra de R$ 500 em cinco " +
                "parcelas de R$ 100, pagar a primeira deixa R$ 400 em aberto. Registre cada " +
                "pagamento. Depois da última parcela, o gasto aparece como Pago.",
        "Em Gastos, encontre a compra e toque em Ver parcelas. Confira o valor e o vencimento da parcela que você pagou. Toque em Registrar pagamento e depois em Confirmar. A barra mostra quanto da compra já foi pago."
    )
)

@Composable
fun TelaEducacaoFinanceira(
    concluidas: Set<Int>,
    onConcluir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var licaoAberta by rememberSaveable { mutableStateOf<Int?>(null) }
    val totalConcluido = licoesFinanceiras.count { it.id in concluidas }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Educação financeira", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "$totalConcluido de 4 lições concluídas",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { totalConcluido / 4f },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(licoesFinanceiras, key = { it.id }) { licao ->
                val concluida = licao.id in concluidas
                FluxoCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Lição ${licao.id} • ${licao.titulo}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            if (concluida) "Concluída" else "Pendente",
                            color = if (concluida) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(onClick = { licaoAberta = licao.id }) { Text("VER LIÇÃO") }
                    }
                }
            }
        }
    }

    licoesFinanceiras.find { it.id == licaoAberta }?.let { licao ->
        AlertDialog(
            onDismissRequest = { licaoAberta = null },
            title = { Text(licao.titulo) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(licao.conteudo)
                    Text("Onde tocar no app", style = MaterialTheme.typography.titleSmall)
                    Text(licao.orientacao)
                }
            },
            confirmButton = {
                if (licao.id !in concluidas) {
                    Button(onClick = {
                        onConcluir(licao.id)
                        licaoAberta = null
                    }) { Text("CONCLUIR") }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { licaoAberta = null }) { Text("FECHAR") }
            }
        )
    }
}
