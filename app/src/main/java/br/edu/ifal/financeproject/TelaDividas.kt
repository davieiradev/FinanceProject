package br.edu.ifal.financeproject

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun TelaDividas(
    dividas: List<Divida>,
    onSalvar: (Divida) -> Unit,
    onExcluir: (Divida) -> Unit,
    modifier: Modifier = Modifier
) {
    var titulo by rememberSaveable {mutableStateOf("")}
    var valor by rememberSaveable {mutableStateOf("")}
    var dataVencimento by rememberSaveable {mutableStateOf("")}
    var erro by rememberSaveable{mutableStateOf<String?>(null)}
    var categoria by rememberSaveable { mutableStateOf("Outros") }
    var paraExcluir by remember { mutableStateOf<Divida?>(null) }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)){

        Text("Nova Dívida", style = MaterialTheme.typography.titleLarge)
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

        SeletorCategoria(categoria, categoriasFrequentes(dividas.map { it.categoria })) { categoria = it }

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
                    onSalvar(Divida(titulo = titulo.trim(), valor = valorNumerico, dataVencimento = dataVencimento.trim(), categoria = categoria))

                    titulo = ""
                    valor = ""
                    dataVencimento = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Registrar Dívida")
        }

        Spacer(Modifier.height(24.dp))

        Text("Minhas Dívidas", style = MaterialTheme.typography.titleMedium)
        Text("Total: ${formatarMoeda(dividas.sumOf { it.valor })}")
        Spacer(Modifier.height(8.dp))

        if (dividas.isEmpty()){
            Text("Nenhuma dívida cadastrada. Que alívio!", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else{
            Column(modifier = Modifier.fillMaxWidth()) {
                dividas.forEach { divida ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical =4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ){
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(divida.titulo, style = MaterialTheme.typography.titleMedium)
                                Text("Vence em ${divida.dataVencimento}", style = MaterialTheme.typography.bodySmall)
                                Text(divida.categoria, style = MaterialTheme.typography.bodySmall)
                            }

                            Text(formatarMoeda(divida.valor), style = MaterialTheme.typography.titleMedium)

                            IconButton(onClick = { paraExcluir = divida }){
                                Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                            }
                        }
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
