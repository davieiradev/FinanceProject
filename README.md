# Fluxo Finance

Aplicativo Android para acompanhar gastos, dívidas, indicadores econômicos e criptomoedas. O app usa armazenamento local para os registros financeiros e consulta serviços externos para os indicadores e as cotações.

## Navegação

| Aba | O que mostra |
|---|---|
| **Resumo** | Totais de gastos e dívidas, últimos lançamentos, alertas por categoria e limite de gastos personalizável. O limite é salvo no dispositivo. |
| **Economia** | Indicadores do Banco Central e acesso à consulta de IPCA. A tela de inflação mostra os seis valores mensais mais recentes da série SGS 433, o acumulado do período e uma comparação visual entre os meses. |
| **Mercado** | Cotações de criptomoedas em USDT, com preço e variação em 24 horas. Os dados vêm da Binance e são atualizados a cada 30 segundos. |
| **Carteira** | Posições demonstrativas armazenadas no dispositivo, com total investido, patrimônio de referência e resultado por ativo. |
| **Dicas** | Dicas financeiras armazenadas localmente, com busca por texto e filtros por assunto. |

## Tecnologias

- **Kotlin e Jetpack Compose** para a interface Android.
- **Room** para persistência local e **SharedPreferences** para o limite de gastos.
- **Retrofit** e **corrotinas Kotlin** para consultas de rede.
- **ViewModel** e **Flow** para o estado das telas.
- **Gradle Kotlin DSL** para configuração do projeto.

## Estrutura do projeto

O código Kotlin do aplicativo fica em `app/src/main/java/br/edu/ifal/financeproject/`.

| Pacote | Responsabilidade |
|---|---|
| `api/` | Serviços e clientes Retrofit do Banco Central e da Binance. |
| `db/` | Banco Room, migrações, DAOs e preferências do limite de gastos. |
| `domain/` | Entidades e modelos usados pelo aplicativo. |
| `ui/screen/` | Telas Compose e `MainActivity`, que organiza a navegação. |
| `ui/components/` | Componentes reutilizáveis, como a logo, os cartões e o seletor de categorias. |
| `ui/theme/` | Paleta, tipografia Nunito e temas claro e escuro. |
| `viewmodel/` | Estado das telas e operações de leitura e atualização dos dados. |
| `util/` | Formatação de valores e datas, totais e regras de alertas. |

Os arquivos JPEG da logo para os temas claro e escuro ficam em `app/src/main/res/drawable/`. Os arquivos da fonte Nunito ficam em `app/src/main/res/font/`.

## Como rodar o projeto

1. Clone o repositório e abra a pasta do projeto no Android Studio.
2. Use o JDK 17 e instale o Android SDK 35. O app usa `compileSdk` e `targetSdk` 35 e suporta dispositivos a partir do Android 7.0 (API 24).
3. Aguarde a sincronização do Gradle.
4. Selecione um emulador ou dispositivo físico e clique em **Run**.

O banco local está na versão 5. Para manter os dados locais, instale a atualização sobre a versão existente do app.

## Testes

No PowerShell, a partir da raiz do projeto, execute:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

Os testes unitários cobrem valores, datas, totais e alertas. Os testes instrumentados verificam as migrações do Room e exigem um emulador ou dispositivo conectado.
