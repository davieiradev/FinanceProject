package br.edu.ifal.financeproject

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.ui.theme.FluxoCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelaDividas(
    dividas: List<Divida>,
    telaAtual: TelaDivida,
    estadoSelic: EstadoSelic,
    onCadastrar: () -> Unit,
    onVoltarParaAcompanhamento: () -> Unit,
    onSalvar: (Divida) -> Unit,
    onAtualizar: (Divida) -> Unit,
    onExcluir: (Divida) -> Unit,
    onAtualizarJuros: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (telaAtual) {
        TelaDivida.CADASTRO -> TelaCadastroDivida(
            onSalvar = onSalvar,
            onVoltar = onVoltarParaAcompanhamento,
            categoriasMaisUsadas = categoriasFrequentes(dividas.map { it.categoria }),
            modifier = modifier
        )
        TelaDivida.ACOMPANHAMENTO -> TelaAcompanhamentoDividas(
            dividas = dividas,
            estadoSelic = estadoSelic,
            onCadastrar = onCadastrar,
            onMarcarComoPaga = { onAtualizar(it.copy(paga = true)) },
            onExcluir = onExcluir,
            onAtualizarJuros = onAtualizarJuros,
            modifier = modifier
        )
    }
}

@Composable
fun TelaCadastroDivida(
    onSalvar: (Divida) -> Unit,
    onVoltar: () -> Unit,
    categoriasMaisUsadas: List<String>,
    modifier: Modifier = Modifier
) {
    var titulo by rememberSaveable {mutableStateOf("")}
    var valor by rememberSaveable {mutableStateOf("")}
    var dataVencimento by rememberSaveable {mutableStateOf("")}
    var erro by rememberSaveable{mutableStateOf<String?>(null)}
    var categoria by rememberSaveable { mutableStateOf("Outros") }
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        IconButton(onClick = onVoltar) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar para acompanhamento")
        }
        Text("Cadastro de dívida", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Registre o que precisa pagar para acompanhar os vencimentos com calma.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it},
            label = { Text("Descrição (Ex: Cartão de Crédito)")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor total (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = dataVencimento,
            onValueChange =  {dataVencimento = it },
            label = {Text ("Vencimento (dd/mm/aaaa)")},
            modifier = Modifier.fillMaxWidth()
        )

        SeletorCategoria(categoria, categoriasMaisUsadas) { categoria = it }

        if (erro!=null){
            Text(erro!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val valorNumerico = interpretarValor(valor)
                if (titulo.isBlank() || valorNumerico == null || interpretarData(dataVencimento) == null){
                    erro = "Informe descrição, valor maior que zero e vencimento válido (dd/mm/aaaa)."
                } else{
                    erro = null
                    onSalvar(
                        Divida(
                            titulo = titulo.trim(),
                            valor = valorNumerico,
                            dataVencimento = dataVencimento.trim(),
                            categoria = categoria
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Registrar Dívida")
        }
    }
}

@Composable
fun TelaAcompanhamentoDividas(
    dividas: List<Divida>,
    estadoSelic: EstadoSelic,
    onCadastrar: () -> Unit,
    onMarcarComoPaga: (Divida) -> Unit,
    onExcluir: (Divida) -> Unit,
    onAtualizarJuros: () -> Unit,
    modifier: Modifier = Modifier
) {
    var paraExcluir by remember { mutableStateOf<Divida?>(null) }
    val pendentes = dividas.filterNot { it.paga }
    val pagas = dividas.filter { it.paga }
    val atualizacoes = (estadoSelic as? EstadoSelic.Sucesso)?.dividasAtualizadas.orEmpty()

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Acompanhamento de dívidas", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Veja o que está pendente e o que já foi resolvido.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onCadastrar) {
                Icon(Icons.Filled.Add, contentDescription = "Cadastrar nova dívida")
            }
        }
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FluxoCard(modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Em aberto", style = MaterialTheme.typography.labelLarge)
                    Text(formatarMoeda(pendentes.sumOf { it.valor }), style = MaterialTheme.typography.titleLarge)
                    Text("${pendentes.size} dívida(s)", style = MaterialTheme.typography.bodySmall)
                }
            }
            FluxoCard(modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Quitado", style = MaterialTheme.typography.labelLarge)
                    Text(formatarMoeda(pagas.sumOf { it.valor }), style = MaterialTheme.typography.titleLarge)
                    Text("${pagas.size} dívida(s)", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        FluxoCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Juros pela Selic diária", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Consulta a série 11 do Banco Central para estimar a correção das dívidas em atraso.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onAtualizarJuros,
                    enabled = estadoSelic !is EstadoSelic.Carregando
                ) {
                    if (estadoSelic is EstadoSelic.Carregando) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(18.dp).height(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Consultando Banco Central...")
                    } else {
                        Text("Calcular juros das atrasadas")
                    }
                }
                when (estadoSelic) {
                    is EstadoSelic.Sucesso -> Text(
                        "Cálculo atualizado para ${atualizacoes.size} dívida(s) em atraso.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    is EstadoSelic.Erro -> Text(
                        estadoSelic.mensagem,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    else -> Unit
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        if (dividas.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Nenhuma dívida cadastrada. Que alívio!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onCadastrar) { Text("Cadastrar dívida") }
            }
        } else{
            Column(modifier = Modifier.fillMaxWidth()) {
                if (pendentes.isNotEmpty()) {
                    Text("Pendentes", style = MaterialTheme.typography.titleMedium)
                    pendentes.forEach { divida ->
                        CardDivida(
                            divida = divida,
                            correcaoSelic = atualizacoes[divida.id],
                            onMarcarComoPaga = onMarcarComoPaga,
                            onExcluir = { paraExcluir = divida }
                        )
                    }
                }
                if (pagas.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Pagas", style = MaterialTheme.typography.titleMedium)
                    pagas.forEach { divida ->
                        CardDivida(divida, null, null, { paraExcluir = divida })
                    }
                }
            }
        }
    }
    paraExcluir?.let { divida ->
        AlertDialog(
            onDismissRequest = { paraExcluir = null },
            title = { Text("Excluir dívida?") },
            text = { Text("${divida.titulo} será removida da lista.") },
            confirmButton = { TextButton(onClick = { onExcluir(divida); paraExcluir = null }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { paraExcluir = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun CardDivida(
    divida: Divida,
    correcaoSelic: DividaAtualizada?,
    onMarcarComoPaga: ((Divida) -> Unit)?,
    onExcluir: () -> Unit
) {
    FluxoCard(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(divida.titulo, style = MaterialTheme.typography.titleMedium)
                Text("${statusDivida(divida)} - vence em ${divida.dataVencimento}", style = MaterialTheme.typography.bodySmall)
                Text(divida.categoria, style = MaterialTheme.typography.bodySmall)
                correcaoSelic?.let { correcao ->
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Estimativa com Selic: ${formatarMoeda(correcao.valorCorrigido)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Juros: ${formatarMoeda(correcao.juros)} em ${correcao.diasComTaxa} dia(s) útil(eis).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (onMarcarComoPaga != null) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { onMarcarComoPaga(divida) }) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Marcar como paga")
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatarMoeda(divida.valor), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onExcluir) {
                    Icon(Icons.Filled.Delete, contentDescription = "Excluir dívida", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

private fun statusDivida(divida: Divida, hoje: Date = Date()): String {
    if (divida.paga) return "Paga"
    val vencimento = interpretarData(divida.dataVencimento) ?: return "Vencimento inválido"
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    val dataHoje = formato.parse(formato.format(hoje)) ?: hoje
    return when {
        vencimento.before(dataHoje) -> "Em atraso"
        vencimento == dataHoje -> "Vence hoje"
        else -> "A vencer"
    }
}
