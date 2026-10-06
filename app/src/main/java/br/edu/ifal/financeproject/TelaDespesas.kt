package br.edu.ifal.financeproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.ui.theme.EstiloValor
import br.edu.ifal.financeproject.ui.theme.FluxoCard

@Composable
fun TelaDespesas(
    despesas: List<Despesa>,
    onExcluir: (Despesa) -> Unit,
    modifier: Modifier = Modifier
) {
    var despesaParaExcluir by remember { mutableStateOf<Despesa?>(null) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Minhas despesas", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Total: ${formatarMoeda(despesas.sumOf { it.valor })}",
            style = EstiloValor,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        if (despesas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Nenhuma despesa por aqui ainda.\nToque em \"Lançar\" para registrar seu primeiro gasto.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(despesas, key = { it.id }) { despesa ->
                    ItemDespesa(despesa, onExcluir = { despesaParaExcluir = despesa })
                }
            }
        }
    }

    despesaParaExcluir?.let { despesa ->
        val corTexto = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        AlertDialog(
            onDismissRequest = { despesaParaExcluir = null },
            title = { Text("Excluir despesa?") },
            text = { Text("\"${despesa.titulo}\" será removida da sua lista.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onExcluir(despesa)
                        despesaParaExcluir = null
                    },
                    colors = corTexto
                ) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { despesaParaExcluir = null }, colors = corTexto) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ItemDespesa(despesa: Despesa, onExcluir: () -> Unit) {
    FluxoCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(despesa.titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    despesa.data,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(formatarMoeda(despesa.valor), style = EstiloValor)
            IconButton(onClick = onExcluir) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Excluir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}