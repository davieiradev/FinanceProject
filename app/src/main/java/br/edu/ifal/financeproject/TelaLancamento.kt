package br.edu.ifal.financeproject

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
fun TelaLancamento(
    onSalvar: (Despesa) -> Unit,
    categoriasMaisUsadas: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var valor by rememberSaveable { mutableStateOf("") }
    var data by rememberSaveable { mutableStateOf(dataHoje()) }
    var erro by rememberSaveable { mutableStateOf<String?>(null) }
    var categoria by rememberSaveable{mutableStateOf("Outros")}

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Fluxo Finance", style = MaterialTheme.typography.headlineMedium)
        Text("Nova despesa", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Descrição") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (R$)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = data,
            onValueChange = { data = it },
            label = { Text("Data (dd/mm/aaaa)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        SeletorCategoria(categoria, categoriasMaisUsadas) { categoria = it }
        Spacer(modifier = Modifier.height(24.dp))

        if (erro != null) {
            Spacer(Modifier.height(8.dp))
            Text(erro!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val valorNumerico = interpretarValor(valor)
                when {
                    titulo.isBlank() -> erro = "Informe uma descrição."
                    valorNumerico == null || valorNumerico <= 0 -> erro = "Informe um valor válido."
                    interpretarData(data) == null -> erro = "Informe uma data válida (dd/mm/aaaa)."
                    else -> {
                        erro = null
                        onSalvar(Despesa(titulo = titulo.trim(), valor = valorNumerico, data = data.trim(), categoria = categoria))
                        titulo = ""
                        valor = ""
                        data = dataHoje()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Gasto")
        }
    }
}
