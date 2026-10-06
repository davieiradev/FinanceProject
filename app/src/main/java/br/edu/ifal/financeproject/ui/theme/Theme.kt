package br.edu.ifal.financeproject.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = VerdeFloresta,            // foco de campos, links, ícones fortes
    onPrimary = VerdeNevoa,
    primaryContainer = VerdeFolha,      // botões principais
    onPrimaryContainer = VerdeFloresta, // texto sobre botão verde folha (contraste 6.4:1)
    secondary = VerdeFloresta,
    onSecondary = VerdeNevoa,
    secondaryContainer = VerdeFolha,    // indicador da barra de navegação
    onSecondaryContainer = VerdeFloresta,
    tertiary = VerdeFolha,
    onTertiary = VerdeFloresta,
    background = VerdeNevoa,
    onBackground = VerdeFloresta,       // contraste 11.5:1
    surface = Branco,
    onSurface = VerdeFloresta,
    surfaceVariant = ContornoSuave,
    onSurfaceVariant = TextoSecundario,
    outline = Contorno,
    outlineVariant = ContornoSuave,
    inverseSurface = VerdeFloresta,
    inverseOnSurface = VerdeNevoa,
    inversePrimary = VerdeFolha,
    surfaceContainerLowest = Branco,
    surfaceContainerLow = Branco,
    surfaceContainer = Branco,
    surfaceContainerHigh = Branco,
    surfaceContainerHighest = ContornoSuave
)

private val DarkColorScheme = darkColorScheme(
    primary = VerdeFolha,
    onPrimary = VerdeFloresta,
    primaryContainer = VerdeFolha,
    onPrimaryContainer = VerdeFloresta,
    secondary = VerdeFolha,
    onSecondary = VerdeFloresta,
    secondaryContainer = VerdeFolha,
    onSecondaryContainer = VerdeFloresta,
    tertiary = VerdeFolha,
    onTertiary = VerdeFloresta,
    background = VerdeFloresta,
    onBackground = VerdeNevoa,
    surface = EscuroSuperficie,
    onSurface = VerdeNevoa,
    surfaceVariant = EscuroContorno,
    onSurfaceVariant = EscuroTextoSecundario,
    outline = EscuroTextoSecundario,
    outlineVariant = EscuroContorno,
    inverseSurface = VerdeNevoa,
    inverseOnSurface = VerdeFloresta,
    inversePrimary = VerdeFloresta,
    surfaceContainerLowest = VerdeFloresta,
    surfaceContainerLow = EscuroSuperficie,
    surfaceContainer = EscuroSuperficie,
    surfaceContainerHigh = EscuroSuperficie,
    surfaceContainerHighest = EscuroContorno
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun FinanceProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}