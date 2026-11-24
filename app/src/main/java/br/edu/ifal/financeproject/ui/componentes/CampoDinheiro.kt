package br.edu.ifal.financeproject.ui.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CampoDinheiro(rotulo: String, centavos: String, aoAlterar: (String) -> Unit) {
    OutlinedTextField(
        value = centavos,
        onValueChange = { entrada ->
            if (entrada.isEmpty() || (entrada.all { it in '0'..'9' } && entrada.toLongOrNull() != null)) {
                aoAlterar(entrada.trimStart('0').ifEmpty { if (entrada.isEmpty()) "" else "0" })
            }
        },
        label = { Text(rotulo) },
        prefix = { Text("R$") },
        textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        ),
        visualTransformation = MascaraReais,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

private object MascaraReais : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        val moeda = NumberFormat.getNumberInstance(Locale("pt", "BR")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
            .format(BigDecimal(text.text).movePointLeft(2))
        val posicoes = moeda.indices.filter { moeda[it].isDigit() }.takeLast(text.length)
        val mapa = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) =
                if (offset == 0) posicoes.first() else posicoes[offset - 1] + 1
            override fun transformedToOriginal(offset: Int) = posicoes.count { it < offset }
        }
        return TransformedText(AnnotatedString(moeda), mapa)
    }
}
