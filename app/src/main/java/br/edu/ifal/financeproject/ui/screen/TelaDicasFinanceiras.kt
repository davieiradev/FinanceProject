package br.edu.ifal.financeproject.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.ifal.financeproject.viewmodel.DicasFinanceirasViewModel
import br.edu.ifal.financeproject.domain.DicaFinanceira

@Composable
fun TelaDicasFinanceiras(
    viewModel: DicasFinanceirasViewModel,
    modifier: Modifier = Modifier
) {
    val dicas by viewModel.dicas.collectAsState()
    var busca by rememberSaveable { mutableStateOf("") }
    var categoriaSelecionada by rememberSaveable { mutableStateOf("Todas") }
    val categorias = remember(dicas) { dicas.map { it.categoria }.distinct().sorted() }
    val dicasFiltradas = remember(dicas, busca, categoriaSelecionada) {
        dicas.filter { dica ->
            val correspondeCategoria = categoriaSelecionada == "Todas" || dica.categoria == categoriaSelecionada
            val correspondeBusca = busca.isBlank() ||
                dica.titulo.contains(busca, ignoreCase = true) ||
                dica.descricao.contains(busca, ignoreCase = true) ||
                dica.categoria.contains(busca, ignoreCase = true)
            correspondeCategoria && correspondeBusca
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)) {
                Text("Guia financeiro", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Ideias simples para cuidar melhor do seu dinheiro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            OutlinedTextField(
                value = busca,
                onValueChange = { busca = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Buscar uma dica") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (listOf("Todas") + categorias).forEach { categoria ->
                    FilterChip(
                        selected = categoriaSelecionada == categoria,
                        onClick = { categoriaSelecionada = categoria },
                        label = { Text(categoria) }
                    )
                }
            }
        }

        item {
            Text(
                "${dicasFiltradas.size} ${if (dicasFiltradas.size == 1) "dica encontrada" else "dicas encontradas"}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (dicasFiltradas.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Nenhuma dica encontrada. Tente outra palavra ou assunto.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        items(dicasFiltradas, key = { it.id }) { dica ->
            CartaoDica(dica)
        }
    }
}

@Composable
private fun CartaoDica(dica: DicaFinanceira) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(dica.categoria.uppercase(), style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary)
            Text(dica.titulo, style = MaterialTheme.typography.titleMedium)
            Text(dica.descricao, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
