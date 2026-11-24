package br.edu.ifal.financeproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class LicaoFinanceira(
    val id: Int,
    val titulo: String,
    val conteudo: String
)

// Cada conteúdo possui menos de 120 palavras.
private val licoesFinanceiras = listOf(
    LicaoFinanceira(
        1,
        "Despesa e dívida",
        "Despesa é um gasto do dia a dia, como transporte, alimentação ou uma compra. " +
                "Dívida é um compromisso de pagamento ainda pendente, como o valor que falta " +
                "pagar de uma compra parcelada. Uma despesa pode ser paga na hora, sem gerar " +
                "dívida. Para organizar as finanças, registre o que gastou e acompanhe " +
                "separadamente o que ainda deve. Assim, você evita confundir dinheiro já " +
                "utilizado com compromissos que vencerão no futuro."
    ),
    LicaoFinanceira(
        2,
        "Limite e saldo",
        "Limite é o valor máximo que você decide utilizar para seus gastos em um período. " +
                "Saldo disponível é a diferença entre esse limite e o total de despesas " +
                "consideradas. Por exemplo: com limite de R$ 1.000 e gastos de R$ 650, " +
                "sobram R$ 350 dentro do limite. Isso não significa necessariamente que " +
                "existam R$ 350 na conta bancária. Confira os valores do aplicativo e " +
                "reserve parte do orçamento para despesas essenciais e imprevistos."
    ),
    LicaoFinanceira(
        3,
        "O que são juros?",
        "Juros são um valor cobrado ou recebido pelo uso do dinheiro durante certo tempo. " +
                "Nos juros simples, o cálculo considera sempre o valor inicial. Nos juros " +
                "compostos, os juros de cada período passam a integrar a base do próximo. " +
                "Por exemplo, R$ 100 com taxa de 10% ao mês viram R$ 110 no primeiro mês " +
                "e R$ 121 no segundo, sem pagamentos intermediários. Antes de assumir uma " +
                "dívida, observe a taxa, o prazo e o custo total."
    ),
    LicaoFinanceira(
        4,
        "Pagar em partes",
        "Quitar uma dívida em partes significa realizar pagamentos parciais até completar " +
                "o valor devido. Em uma dívida de R$ 500, pagar R$ 200 deixa R$ 300 restantes, " +
                "se não houver novos juros ou taxas. Registre cada pagamento para acompanhar " +
                "quanto foi pago e quanto falta. Planeje valores que caibam no orçamento " +
                "e verifique as condições reais da dívida: algumas cobranças continuam " +
                "gerando juros enquanto existe saldo pendente."
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
                Card(modifier = Modifier.fillMaxWidth()) {
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
                        TextButton(onClick = {
                            licaoAberta = if (licaoAberta == licao.id) null else licao.id
                        }) {
                            Text(if (licaoAberta == licao.id) "Ocultar lição" else "Ler lição")
                        }

                        if (licaoAberta == licao.id) {
                            Text(licao.conteudo, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            TextButton(
                                enabled = !concluida,
                                onClick = { onConcluir(licao.id) }
                            ) {
                                Text(if (concluida) "Já concluída" else "Marcar como concluída")
                            }
                        }
                    }
                }
            }
        }
    }
}
