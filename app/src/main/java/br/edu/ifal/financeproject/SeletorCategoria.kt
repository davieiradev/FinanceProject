package br.edu.ifal.financeproject

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SeletorCategoria(categoria: String, frequentes: List<String>, onSelecionar: (String) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    Column {
        if (frequentes.isNotEmpty()) {
            Text("Categorias frequentes")
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                frequentes.forEach { opcao ->
                    FilterChip(selected = categoria == opcao, onClick = { onSelecionar(opcao) }, label = { Text(opcao) })
                }
            }
        }
        OutlinedButton(onClick = { expandido = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Categoria: $categoria ▾")
        }
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            categoriasDisponiveis.forEach { opcao ->
                DropdownMenuItem(text = { Text(opcao) }, onClick = {
                    onSelecionar(opcao)
                    expandido = false
                })
            }
        }
    }
}
