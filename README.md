# Fluxo Finance

Aplicativo Android de controle de gastos, simples e acolhedor, feito para quem quer organizar a vida financeira (inclusive quem está endividado). O app permite registrar despesas, acompanhá-las em tempo real e ter uma visão geral do que foi gasto, com persistência local e arquitetura moderna de Android.

## Funcionalidades
- **Limite de gastos personalizável**: na aba Resumo, toque em **Definir limite** ou **Alterar limite**. O valor fica salvo no dispositivo e atualiza o saldo disponível e a barra de progresso, considerando todas as despesas cadastradas.
- **Cadastro de despesas** (descrição, valor e data), com validação dos campos e aceitação de vírgula ou ponto no valor.
- **Listagem em tempo real** de todas as despesas, da mais recente para a mais antiga, com o total gasto no topo.
- **Exclusão de despesas**, com confirmação antes de remover.
- **Resumo geral**: total gasto, quantidade de despesas e os 3 últimos lançamentos.
- **Navegação por abas** (Resumo, Despesas, Dívidas e Lançar) e avisos rápidos (Snackbar) de confirmação.
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
O app tem quatro abas, acessíveis pela barra de navegação inferior:
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

O banco está na versão 3. As migrações preservam despesas das versões 1 e 2, criam a tabela de dívidas quando necessário e atribuem a categoria "Outros" às dívidas antigas. Atualize o aplicativo sem desinstalá-lo para manter os dados locais.


## Iteração 3 — Dívidas, totais, atalhos e alertas

- **#7 Cadastro de dívidas:** descrição/credor, valor positivo com até duas casas, vencimento válido em dd/mm/aaaa e categoria. Lista da mais recente para a mais antiga, total e exclusão com confirmação. Despesas e dívidas ficam no Room.
- **#8 Totais acumulados:** o Resumo mostra a soma de todos os gastos, a soma de todas as dívidas cadastradas e a soma dos dois valores. Atualiza ao inserir/excluir. Não há filtro de período nesta visão acumulada. Dívidas não reduzem o limite de gastos; não registre a mesma obrigação nas duas listas se não quiser somá-la duas vezes.
- **#10 Atalhos:** até três categorias com pelo menos dois registros, ordenadas por frequência e depois por nome. Históricos independentes para despesas e dívidas. Tocar no atalho seleciona a categoria; os atalhos são recalculados ao excluir registros e após reabrir, a partir dos dados do Room.
- **#11 Avisos visuais:** banner no topo do Resumo, logo abaixo da logo, com ícone, borda e fundo destacados. Aparece a partir de três registros na mesma categoria Lazer ou Outros, somando gastos dos últimos sete dias de calendário (hoje + seis dias anteriores) e todas as dívidas em aberto. Gastos futuros ou com datas inválidas não contam; dívidas pagas não contam. Dívidas com vencimento futuro contam porque vencimento não é data de criação. O banner mostra quantidade e valor de gastos e dívidas separadamente, além do total por categoria. É uma regra inicial de atenção, com texto acolhedor. Excluir registros recalcula o aviso. Não são notificações em segundo plano.
- O limite personalizável continua nas preferências locais; a migração para Room e demais ajustes da iteração 2 não fazem parte desta alteração. O campo paga da dívida permanece reservado; não há baixa de pagamento ou progresso de quitação nesta iteração.

### Verificação da iteração 3

1. Instalar a atualização sobre o app existente, sem limpar dados. Verificar despesas antigas.
2. Em Dívidas, tentar salvar campos vazios, valor zero e 31/02/2026: mostrar erro sem cadastrar.
3. Cadastrar duas dívidas em Educação (100 e 200 reais). Verificar lista, total de 300 reais e atalho Educação. Fechar/reabrir e confirmar dados e atalho.
4. Registrar dois gastos em Transporte e verificar seu atalho no formulário de gastos.
5. No Resumo, conferir gastos + dívidas à mão. Excluir uma dívida com confirmação e conferir redução exata.
6. Registrar dois gastos em Lazer dentro dos últimos sete dias e uma dívida em aberto em Lazer. Verificar o banner no topo, a contagem de três registros e os valores separados. Excluir a dívida e verificar desaparecimento do aviso, se não houver outros registros elegíveis. Conferir também que três dívidas em aberto em Outros geram um aviso mesmo sem gastos.
7. Verificar que gastos antigos, futuros e três gastos apenas em Alimentação não acionam essa regra.
8. Repetir cadastro e reabertura em modo avião. Testar formulários com teclado aberto e fonte ampliada.

Testes automatizados: ValorTest e AnaliseFinanceiraTest cobrem valores, datas, totais, frequência, limites da janela e limiar dos alertas. MigracaoTest cobre bancos v1 e as variantes v2, preservação de registros e persistência de dívidas após reabertura. Os testes instrumentados exigem emulador/dispositivo.
