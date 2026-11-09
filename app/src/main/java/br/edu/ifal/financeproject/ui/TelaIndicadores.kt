package br.edu.ifal.financeproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
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
import br.edu.ifal.financeproject.network.RetrofitClient

@Composable
fun TelaIndicadores(
    modifier: Modifier = Modifier,
    onAbrirIpca: () -> Unit
){
    var taxaSelic by remember{mutableStateOf("A carregar...")}
    var taxaSelicNum by remember { mutableStateOf(0.0) }
    var dataAtualizacao by remember {mutableStateOf("")}
    var erro by remember {mutableStateOf<String?>(null)}

    val valorInvestidoMock = 1000.0

LaunchedEffect(Unit){
    try {
        val resposta = RetrofitClient.service.getUltimaSelic()
        if (resposta.isNotEmpty()){
            taxaSelic = resposta[0].valor + "%"
            taxaSelicNum = resposta[0].valor.toDoubleOrNull() ?: 0.0
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
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Top
) {
    Text("Indicadores Econômicos", style = MaterialTheme.typography.headlineMedium)
    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = onAbrirIpca) {
        Text("Consultar inflação IPCA")
    }
    Spacer(modifier = Modifier.height(16.dp))

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

    Spacer(modifier = Modifier.height(24.dp))

    if (taxaSelicNum > 0.0){
        val rendimentoAtual = valorInvestidoMock * (taxaSelicNum/100)
        val valorTotal = valorInvestidoMock + rendimentoAtual

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Simulador de Rendimento", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Se investir R$1000,00 hoje:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

                Text(
                    text = "Em 1 ano terá: R$ ${String.format("%.2f", valorTotal)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

            Text(
                text = "(Rendeu R$ ${String.format("%.2f", rendimentoAtual)})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
}