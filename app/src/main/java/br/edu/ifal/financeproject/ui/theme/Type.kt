package br.edu.ifal.financeproject.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.edu.ifal.financeproject.R

val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold)
)

private fun estilo(peso: FontWeight, tamanho: Int, altura: Int, espaco: Double = 0.0) = TextStyle(
    fontFamily = Nunito,
    fontWeight = peso,
    fontSize = tamanho.sp,
    lineHeight = altura.sp,
    letterSpacing = espaco.sp
)

val Typography = Typography(
    displayLarge = estilo(FontWeight.ExtraBold, 57, 64),
    displayMedium = estilo(FontWeight.ExtraBold, 45, 52),
    displaySmall = estilo(FontWeight.ExtraBold, 36, 44),

    headlineLarge = estilo(FontWeight.ExtraBold, 32, 40),
    headlineMedium = estilo(FontWeight.ExtraBold, 28, 36),
    headlineSmall = estilo(FontWeight.ExtraBold, 24, 32),

    titleLarge = estilo(FontWeight.ExtraBold, 22, 28),
    titleMedium = estilo(FontWeight.Bold, 16, 24, 0.15),
    titleSmall = estilo(FontWeight.Bold, 14, 20, 0.1),

    bodyLarge = estilo(FontWeight.Normal, 16, 24, 0.5),
    bodyMedium = estilo(FontWeight.Normal, 14, 20, 0.25),
    bodySmall = estilo(FontWeight.Normal, 12, 16, 0.4),

    labelLarge = estilo(FontWeight.Bold, 14, 20, 0.1),
    labelMedium = estilo(FontWeight.SemiBold, 12, 16, 0.5),
    labelSmall = estilo(FontWeight.SemiBold, 11, 16, 0.5)
)

val EstiloValor = estilo(FontWeight.Bold, 18, 24)
val EstiloValorGrande = estilo(FontWeight.Bold, 34, 42)