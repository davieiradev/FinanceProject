# Fluxo Finance

Aplicativo Android para registrar gastos, acompanhar dívidas e organizar o orçamento. Os dados ficam no dispositivo.

## Como usar

- **Lançar** reúne o cadastro de gastos à vista e a prazo. Informe a descrição, o valor total e a categoria. Selecione as datas pelo calendário. Os valores usam teclado numérico decimal e aparecem alinhados à direita com R$.
- **À vista** permite informar a data do gasto ou ativar **Tem vencimento**. Todos aparecem em **Gastos**.
- **A prazo** pede o número de parcelas e o vencimento da primeira. As seguintes vencem no mesmo dia dos meses seguintes. Se o dia não existir naquele mês, vale o último dia do mês. O valor total é dividido em centavos entre as parcelas.
- **Gastos** reúne compras à vista e parcelas, com filtros por categoria e período, total e gráfico. Para filtrar por período, a tela usa a data do gasto à vista ou o vencimento da obrigação. Gastos com vencimento permitem registrar pagamentos e consultar o saldo restante.
- **Resumo** mostra totais, limite de gastos e alertas de recorrência. Gastos à vista sem vencimento e cada pagamento registrado reduzem o saldo do limite. É possível ativar lembretes quando o app exibe um alerta.

## Tecnologias

Kotlin, Android, Jetpack Compose, Room, WorkManager e Gradle Kotlin DSL.

## Estrutura do projeto

A organização do módulo Android segue o [AulaPRMO](https://github.com/tarsisms/AulaPRMO).

- `app/src/main/java/br/edu/ifal/financeproject/` contém a atividade principal e o verificador periódico de alertas.
- No pacote do aplicativo, `ui/telas/` contém as três abas, `ui/componentes/` reúne os componentes reutilizáveis e `ui/viewmodel/` contém o ViewModel de gastos.
- `dados/` contém o banco, os DAOs e as preferências, com as entidades em `dados/modelo/`.
- `financas/` reúne as regras de análise e a formatação. `notificacoes/` contém o envio de lembretes.
- `app/src/main/java/br/edu/ifal/financeproject/ui/theme/` contém o tema e os componentes visuais.
- `app/src/main/res/` reúne os recursos do aplicativo.
- `app/src/test/` e `app/src/androidTest/` contêm os testes locais e instrumentados.
- `gradle/` contém a configuração e o wrapper do Gradle.
- `apresentacao/` reúne o material de apresentação e a imagem da marca.

## Executar localmente

1. Clone o repositório e abra a pasta no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Execute o módulo `app` em um emulador ou dispositivo Android 7.0 (API 24) ou superior. O projeto compila com a API 35.

O banco local usa Room e está na versão 6. Instale a atualização sobre a versão anterior para manter os dados locais.
