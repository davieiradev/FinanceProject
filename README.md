# Fluxo Finance

Aplicativo Android de controle de gastos, simples e acolhedor, feito para quem quer organizar a vida financeira (inclusive quem está endividado). O app permite registrar despesas, acompanhá-las em tempo real e ter uma visão geral do que foi gasto, com persistência local e arquitetura moderna de Android.

## Funcionalidades
- **Cadastro de despesas** (descrição, valor e data), com validação dos campos e aceitação de vírgula ou ponto no valor.
- **Listagem em tempo real** de todas as despesas, da mais recente para a mais antiga, com o total gasto no topo.
- **Exclusão de despesas**, com confirmação antes de remover.
- **Resumo geral**: total gasto, quantidade de despesas e os 3 últimos lançamentos.
- **Navegação por abas** (Resumo, Despesas e Lançar) e avisos rápidos (Snackbar) de confirmação.
- **Armazenamento local**: as informações não são perdidas ao fechar o app.
- **Identidade visual própria**: paleta, fonte, logo e ícone do aplicativo seguindo o Guia de Identidade Visual do projeto.

## Tecnologias e Ferramentas Utilizadas
- **Kotlin**: linguagem de programação principal.
- **Jetpack Compose**: utilizado para a construção da interface gráfica (UI) de forma declarativa.
- **Room Database**: biblioteca do Jetpack para abstração e persistência de dados SQLite no dispositivo.
- **ViewModel**: para o gerenciamento de estados da tela, sobrevivendo a mudanças de configuração.
- **Arquitetura MVVM**: padrão utilizado para separar a lógica de negócios da interface.
- **Gradle Kotlin DSL**: gerenciamento de dependências usando scripts `.kts`.

## Estrutura do Projeto
A lógica central fica em `app/src/main/java/br/edu/ifal/financeproject/`:

| Arquivo | Responsabilidade |
|---|---|
| `Despesa.kt` | Entity do Room: tabela de despesas (`id`, `titulo`, `valor`, `data`). |
| `DespesaDao.kt` | DAO com `inserir`, `deletar` e `listarTodas()` (retorna `Flow`, ordenada da mais nova para a mais antiga). |
| `AppDataBase.kt` | Configuração e inicialização do Room Database (singleton). |
| `DespesaViewModel.kt` | Expõe a lista de despesas (`StateFlow`) e as ações de adicionar e excluir. |
| `MainActivity.kt` | Ponto de entrada: `Scaffold` com a barra de navegação inferior, as abas e a tela de lançamento (`EcraAdicionarDespesa`). |
| `TelaResumo.kt` | Tela de resumo: logo, total gasto e últimos lançamentos. |
| `TelaDespesas.kt` | Tela com a lista de despesas, total e exclusão com confirmação. |
| `Formatadores.kt` | Funções utilitárias: formatação de moeda (R$, pt-BR) e data de hoje. |
| `ui/theme/` | Tema global (veja abaixo). |

### Tema (`ui/theme/`)
| Arquivo | Conteúdo |
|---|---|
| `Color.kt` | Paleta oficial (verde floresta, verde folha, verde névoa) e neutros de apoio. |
| `Type.kt` | Família Nunito e estilos de texto (títulos ExtraBold, texto Regular, valores Bold). |
| `Theme.kt` | `FinanceProjectTheme`: esquemas de cor claro e escuro, formas arredondadas. Sem cores dinâmicas, para manter a identidade da marca. |
| `Componentes.kt` | Componentes reutilizáveis: `FluxoCard`, `botaoFluxoColors()` e `LogoFluxo()`. |

### Recursos (`app/src/main/res/`)
- `font/`: arquivos da fonte Nunito.
- `drawable/`: logo em vetor (`ic_logo`, `ic_logo_escuro`) e camadas do ícone do app.

## Telas
O app tem três abas, acessíveis pela barra de navegação inferior:
- **Resumo (início)**: logo, total gasto até o momento, quantidade de despesas e os 3 últimos lançamentos.
- **Despesas**: lista completa em cards (descrição, data e valor), com total no topo. Cada item pode ser excluído, com confirmação.
- **Lançar**: formulário para registrar uma nova despesa. Após salvar, o app leva para a aba Despesas e mostra uma confirmação.

## Identidade Visual
O app segue o *Guia de Identidade Visual do Fluxo Finance*:
- **Cores**: Verde floresta `#253916` (textos e base), Verde folha `#65D067` (botões, ícones e destaques) e Verde névoa `#F2F6F0` (fundo das telas). O verde folha nunca é usado como cor de texto, por causa do contraste.
- **Tipografia**: Nunito.
- **Logo**: dois FF lado a lado, com versões para fundo claro e escuro.
- **Tom de voz**: linguagem simples, acolhedora e nunca de cobrança (por exemplo, "Bom trabalho!" em vez de alertas ameaçadores).

## Como rodar o projeto localmente
1. Clone este repositório na sua máquina.
2. Abra a pasta do projeto no **Android Studio** (use o JDK 17, que o projeto exige).
3. Aguarde o Gradle finalizar a sincronização (Sync).
4. Clique em "Run" para compilar e executar em um dispositivo físico ou emulador (Virtual Device).

Requisitos: `minSdk` 24 (Android 7.0) e `compileSdk`/`targetSdk` 35.
