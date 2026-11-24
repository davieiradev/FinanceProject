# Fluxo Finance

Aplicativo Android para registrar gastos, acompanhar parcelas e organizar o orçamento. Os dados ficam no dispositivo.

## Como usar

- Em **Lançar**, informe descrição, valor total, data e categoria. Escolha **À vista** ou **A prazo**.
- Para compras a prazo, informe a quantidade de parcelas e escolha o primeiro vencimento no calendário. Escolha se há juros. Para compras com juros, informe o preço sem juros e o valor total dos juros em reais. O app divide a soma entre as parcelas. A última recebe os centavos restantes.
- Em **Gastos**, abra as parcelas de uma compra para conferir os vencimentos e registrar pagamentos. Com os lembretes ativados, o app avisa quando faltam até três dias para uma parcela em aberto vencer.
- Em **Gastos**, selecione o período para consultar lançamentos e o gráfico por categoria. Em **Resumo**, acompanhe o saldo, veja os alertas e abra o simulador de juros.

## Glossário

| Termo | Como funciona no aplicativo |
| --- | --- |
| Gasto à vista | Reduz o saldo disponível no lançamento. |
| Gasto a prazo | Compra com uma ou mais parcelas. Cada pagamento reduz o saldo pelo valor da parcela. |
| Vencimento | Dia previsto para pagar a parcela. Se o mês for mais curto, vale seu último dia. |
| Pago | Gasto à vista registrado ou compra a prazo com todas as parcelas pagas. |
| Limite | Valor definido pelo usuário para acompanhar os gastos. |
| Saldo disponível | Limite menos os gastos à vista e as parcelas já pagas. |

## Tecnologias

Kotlin, Android, Jetpack Compose, Room, WorkManager e Gradle Kotlin DSL.

## Estrutura do projeto

A organização do módulo Android segue o [AulaPRMO](https://github.com/tarsisms/AulaPRMO).

- `app/src/main/java/br/edu/ifal/financeproject/` contém a atividade principal e o verificador periódico de alertas.
- No pacote do aplicativo, `ui/telas/` contém as quatro abas e `ui/componentes/` reúne os controles reutilizáveis.
- `dados/` contém o banco e as preferências, com os DAOs em `dados/dao/`, com as entidades em `dados/modelo/`.
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

O banco local usa Room na versão 9. A migração da versão 8 preserva os gastos e as parcelas e inicializa os juros em zero. A atualização recria o banco de exemplos das versões anteriores à 8, conforme o item #19.
