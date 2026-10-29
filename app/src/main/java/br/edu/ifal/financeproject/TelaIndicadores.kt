package br.edu.ifal.financeproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun TelaIndicadores(modifier: Modifier = Modifier){
    var taxaSelic by remember{mutableStateOf("A carregar...")}
    var dataAtualizacao by remember {mutableStateOf("")}
    var erro by remember {mutableStateOf<String?>(null)}

LaunchedEffect(Unit){
    try {
        val resposta = RetrofitClient.service.getUltimaSelic()
        if (resposta.isNotEmpty()){
            taxaSelic = resposta[0].valor + "%"
            dataAtualizacao = resposta[0].data
        }
    } catch (e: Exception) {
        erro = "Falha na ligação: ${e.message}"
        taxaSelic = "--"
    }
}

Column(
    modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Text("Indicadores Econômicos", style = MaterialTheme.typography.headlineMedium)
    Spacer(modifier = Modifier.height(32.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Taxa Selic Atual", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = taxaSelic,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            if (dataAtualizacao.isNotEmpty()){
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Última atualização: $dataAtualizacao",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (erro!= null){
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = erro!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
}