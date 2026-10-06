package br.edu.ifal.financeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[DespesaViewModel::class.java]

        setContent{
            MaterialTheme{
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                    ) {
                    EcraAdicionarDespesa(viewModel = viewModel)
                }


            }
        }
    }
}

@Composable
fun EcraAdicionarDespesa(viewModel: DespesaViewModel){
    var titulo by remember {mutableStateOf("")}
    var valor by remember {mutableStateOf("")}
    var data by remember {mutableStateOf("")}

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text="Nova Despesa", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value =  titulo,
            onValueChange = {titulo = it},
            label = {Text("O que Comprou?")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (Ex: 50.0)")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = data,
            onValueChange = {data = it },
            label = {Text("Data (Ex: XX/XX/XXXX)")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer( modifier = Modifier.height(16.dp))
        Button(onClick = {
        val valorDouble = valor.toDoubleOrNull()?: 0.0

        viewModel.adicionarDespesa(titulo, valorDouble, data)

            titulo = ""
            valor = ""
            data = ""

        },
        modifier = Modifier.fillMaxWidth()
            ) {
            Text("Guardar Despesas")
        }
    }
}