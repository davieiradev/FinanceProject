package br.edu.ifal.financeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            MaterialTheme{
                TelaLancamentoBasico()
            }
        }
    }
}

@Composable
fun TelaLancamentoBasico(){
    var valorGasto by remember {mutableStateOf("")}

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text="Fluxo Finance", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = valorGasto,
            onValueChange = {valorGasto = it},
            label = {Text("Valor do Gasto (R$)")}
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            println("o valor salvo foi: $valorGasto")
        }) {
            Text("Guardar Gasto")
        }
    }
}