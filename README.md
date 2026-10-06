# Finance Project

Aplicativo Android focado no gerenciamento de despesas pessoais. O objetivo deste projeto inicial é gerenciar finanças de forma simples e rápida, utilizando persistência de dados local e padrões de arquitetura modernos do desenvolvimento Android.

## Funcionalidades
- Cadastro de novas despesas.
- Listagem em tempo real de todas as despesas cadastradas.
- Armazenamento local de dados (as informações não são perdidas ao fechar o app).

## Tecnologias e Ferramentas Utilizadas
O projeto foi desenvolvido com foco no ecossistema atual do Android:
- **Kotlin**: Linguagem de programação principal.
- **Jetpack Compose**: Utilizado para a construção da interface gráfica (UI) de forma declarativa.
- **Room Database**: Biblioteca do Jetpack para abstração e persistência de dados SQLite no dispositivo.
- **ViewModel**: Para o gerenciamento de estados da tela, sobrevivendo a mudanças de configuração.
- **Arquitetura MVVM**: Padrão utilizado para separar a lógica de negócios da interface.
- **Gradle Kotlin DSL**: Gerenciamento de dependências usando scripts `.kts`.

## Estrutura do Projeto
A lógica central da aplicação está localizada no diretório `app/src/main/java/br/edu/ifal/financeproject/`.

- `Despesa.kt`: Classe de dados (Entity) que representa a tabela de despesas no banco.
- `DespesaDao.kt`: Interface (Data Access Object) com os métodos de manipulação do banco (inserir, listar).
- `AppDataBase.kt`: Classe de configuração e inicialização do Room Database.
- `DespesaViewModel.kt`: Gerencia os dados da UI e se comunica com o DAO, mantendo a reatividade.
- `MainActivity.kt`: Ponto de entrada da aplicação, onde a interface construída em Compose é chamada.
- `ui/theme/`: Diretório que guarda as configurações visuais globais (Colors, Typography, Theme).

## Telas
Nesta versão inicial, o app é composto basicamente por:
- **Tela Principal (Home)**: Exibe a lista de despesas já registradas no banco de dados. A partir dessa tela, o usuário pode interagir com os campos de entrada (formulário) para adicionar uma nova despesa, que é automaticamente refletida na lista abaixo graças ao uso do ViewModel.

## Como rodar o projeto localmente
1. Clone este repositório na sua máquina.
2. Abra a pasta do projeto no **Android Studio**.
3. Aguarde o Gradle finalizar a sincronização (Sync).
4. Clique em "Run" para compilar e emular o projeto em um dispositivo físico ou emulador (Virtual Device).
