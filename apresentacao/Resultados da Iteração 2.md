# FluxoFinance — Resultados da Iteração 2

Roteiro elaborado a partir dos dois PDFs fornecidos e do código atual. Campos entre colchetes dependem de confirmação da equipe. Não representam resultados já obtidos.

## Conferência do plano

| Item do backlog (texto original) | Situação no código | Evidência e ressalva |
|---|---|---|
| Estruturar o banco de dados para salvar o limite disponível | Parcial em relação ao texto literal | LimitePreferencias.kt salva o limite em SharedPreferences. MainActivity.kt recupera e atualiza o valor. O comportamento de persistência está implementado, mas o limite não está no Room. |
| Permitir a seleção de categorias na adição das despesas | Implementado | TelaLancamento.kt oferece Alimentação, Transporte, Lazer, Educação e Outros e envia a categoria escolhida no objeto Despesa. Despesa.kt contém o campo; o DAO grava a entidade. A confirmação de persistência no aparelho ainda precisa ser documentada. |
| Exibir uma barra mostrando o saldo atual a cada adição de despesa | Implementado, com esclarecimento | TelaResumo.kt mostra o saldo numericamente e uma barra de proporção consumida do limite. MainActivity.kt observa as despesas via Flow. A barra cresce com os gastos; ela não representa diretamente a proporção restante. |
| Exibir o ícone da logo na inicialização do aplicativo e no menu dos dispositivos | Recursos e configuração presentes; validação visual pendente | O Manifest aponta para os ícones personalizados, os arquivos WebP contêm a logo FF e o ícone adaptativo referencia o foreground personalizado. LogoFluxo também aparece no Resumo. Não há splash screen personalizada explícita: o comportamento da abertura precisa ser confirmado nas versões Android usadas na demonstração. |

O plano também alterna os termos “limite disponível” e “saldo disponível”. No app, limite é o teto escolhido; saldo é limite menos a soma das despesas. O saldo é calculado, não salvo separadamente. Não há filtro mensal, apesar do nome limiteMensal no código.

O slide “Resultados da Iteração” do plano já contém quatro marcas de conclusão. Essas marcas não substituem a verificação do código, a demonstração e o aceite.

## Conteúdo para os slides

### Slide 1 — Capa

FluxoFinance
Apresentação dos Resultados da Iteração 2
Equipe: Gabriel Sousa, José Davi, Jefferson Santos, Daniel Araújo e José Arthur
Data da apresentação: 13/10/2026

### Slide 2 — Agenda

- Itens planejados para a iteração
- Itens entregues na iteração
- Itens apresentados na iteração
- Validação do produto
- Métricas da iteração
- Lições aprendidas da iteração

### Slide 3 — Itens planejados para a iteração

Manter os itens iguais aos do backlog do plano:

- Estruturar o banco de dados para salvar o limite disponível.
- Permitir a seleção de categorias na adição das despesas.
- Exibir uma barra mostrando o saldo atual a cada adição de despesa.
- Exibir o ícone da logo na inicialização do aplicativo e no menu dos dispositivos.

Nota para a fala: o objetivo da iteração era aprimorar o armazenamento local das finanças, a categorização das despesas e a identidade visual.

### Slide 4 — Itens entregues

- Seleção de categorias no cadastro de despesas, com gravação do campo no Room.
- Atualização do saldo e da barra de consumo do limite a partir das despesas cadastradas.
- Ícones personalizados com a logo configurados para o aplicativo e logo exibida no Resumo. [Confirmar abertura e menu no aparelho antes de marcar esse item como concluído.]

Entrega funcional com divergência do plano:

- Definição, alteração e persistência local do limite implementadas com SharedPreferences. O requisito de armazená-lo no banco Room permanece pendente, salvo aprovação explícita dessa alteração de solução.

Nota para a fala: separar “a funcionalidade funciona” de “o requisito foi cumprido exatamente”. Não dizer que o limite foi salvo no Room.

### Slide 5 — Itens apresentados

- Os itens apresentados na iteração 1 foram aceitos pelo professor, conforme informado pelo desenvolvedor.
- Os resultados da iteração 2 serão apresentados ao professor em 13/10/2026.
- A demonstração desta iteração incluirá limite personalizável, seleção de categorias, saldo, barra de progresso e identidade visual.
- O aceite dos itens da iteração 2 será registrado após a apresentação; o aceite anterior não é contabilizado como aceite desta iteração.

### Slide 6 — Validação dos itens

Validação com evidência disponível:

- Compilação da versão de desenvolvimento concluída com sucesso na verificação anterior.
- Dois testes automatizados de interpretação de valores passaram, sem falhas: entradas válidas e entradas inválidas.
- O desenvolvedor confirmou o funcionamento após executar a versão atualizada.

Validação a complementar antes da apresentação:

- Definir e alterar o limite; fechar e reabrir o app para verificar a persistência.
- Salvar uma despesa com categoria escolhida e verificar que ela foi armazenada corretamente.
- Comparar saldo e barra com valores calculados manualmente.
- Verificar a logo no menu do dispositivo e durante uma abertura a frio do app.

Nota para a fala: os testes de valores não comprovam, sozinhos, todos os quatro itens do backlog. O teste de migração foi compilado, mas não executado no dispositivo; não contabilizá-lo como teste aprovado.

### Slide 7 — Métricas da iteração (atualizado com o backlog)

- Backlog completo: 21 histórias.
- Escopo considerado para esta métrica: 19 histórias, excluindo IDs 20 e 21, explicitamente fora do escopo atual. IDs 15–19 são opcionais e foram incluídos no denominador; se a equipe não os considerar parte da entrega, deverá declarar outro recorte.
- Iteração 1: IDs 1–4 constam como Feito e SIM. Progresso registrado: 4/19 = 21,05% por contagem de histórias, sem ponderação por esforço.
- Iteração 2 no backlog: IDs 5, 6 e 9, três histórias. Os quatro itens do plano são tarefas e não equivalem a quatro novas histórias.
- Não há evidência suficiente para declarar as três histórias da iteração 2 concluídas integralmente. Elas possuem funcionalidades implementadas, mas critérios ou validações pendentes.
- Aceite externo da iteração 2: pendente da apresentação em 13/10/2026. Os responsáveis internos pelo aceite das histórias 5, 6 e 9 também estão A definir no documento.
- Testes de valores: 2/2 aprovados, 100% somente dessa suíte.
- Validação das histórias e participação em reuniões: percentuais ainda não apurados.

Cenário futuro, condicionado à conclusão das três histórias: contribuição da iteração 2 = 3/19 = 15,79 pontos percentuais; progresso acumulado = 7/19 = 36,84%; entrega das histórias da iteração = 3/3 = 100%. Esses números não são o resultado atual comprovado.

Se a disciplina exigir a contagem das 21 linhas, sem excluir itens fora do escopo, os valores correspondentes são: progresso anterior 4/21 = 19,05%; contribuição de três histórias 3/21 = 14,29 pontos percentuais; acumulado 7/21 = 33,33%. Escolher e declarar um único denominador.

### Slide 8 — Lições aprendidas

Sugestões para a equipe confirmar como aprendizados reais:

- Registrar critérios de conclusão antes da implementação, incluindo a forma de armazenamento exigida.
- Distinguir limite definido pelo usuário de saldo calculado após as despesas.
- Centralizar a validação de valores para manter as mesmas regras nos diferentes formulários.
- Usar estado observado pelo Compose para atualizar o resumo quando o limite ou as despesas mudam.
- Verificar recursos visuais também no dispositivo, além de conferir sua configuração no projeto.
- Registrar demonstrações, aceite, testes e participação da equipe durante a iteração para produzir métricas verificáveis.

## Roteiro de demonstração e registro de resultados

Usar despesas de demonstração separadas dos registros pessoais. Os resultados abaixo são esperados, não testes já executados.

1. Anotar o total gasto atual, G. Definir o limite como G + R$ 1.000,00. Saldo esperado: R$ 1.000,00.
2. Fechar e reabrir o aplicativo. Limite esperado: o mesmo definido no passo anterior.
3. Adicionar R$ 100,00 em Alimentação. Saldo esperado: R$ 900,00; gasto total esperado: G + R$ 100,00; barra esperada: (G + 100)/(G + 1000).
4. Adicionar R$ 50,00 em Transporte. Saldo esperado: R$ 850,00; barra esperada: (G + 150)/(G + 1000).
5. Excluir essas duas despesas de demonstração. Saldo esperado: R$ 1.000,00.
6. Alterar o limite para G + R$ 500,00. Saldo esperado: R$ 500,00.
7. Tentar salvar limite vazio, zero e negativo. Resultado esperado: mensagem de validação e manutenção do limite anterior.
8. Conferir a categoria armazenada usando a inspeção do banco no Android Studio. A tela de listagem atual não exibe a categoria, portanto ela não serve, sozinha, para comprovar o campo persistido.
9. Mostrar o ícone no menu do aparelho e realizar abertura a frio. Registrar a versão Android e uma captura ou vídeo.

Para uma demonstração com dados limpos e de teste, usar limite de R$ 1.000,00 e despesas de R$ 100,00 e R$ 50,00: saldo R$ 850,00 e barra em 15%. Não apagar dados pessoais apenas para obter esse exemplo.

Registro sugerido por item: critério verificado | resultado observado | evidência | responsável | data | aceite do cliente.

## Informações ainda necessárias

- Registrar após a apresentação quais itens da iteração 2 foram efetivamente demonstrados e aceitos pelo professor.
- Confirmação visual da logo na abertura e no menu do dispositivo.
- Decisão sobre a divergência SharedPreferences versus banco Room.
- Quantidade de itens no backlog total e quantidade concluída antes desta iteração.
- Reuniões efetivamente realizadas e presença dos integrantes.

## Revisão a partir do backlog do produto fornecido em 13/10/2026

Esta revisão substitui qualquer interpretação anterior de que apenas persistência do limite e confirmação do ícone bastariam para concluir todo o escopo da iteração 2.

### História 5 — Lançamento rápido de gastos

Há formulário, seleção de categoria, armazenamento das despesas com categoria, listagem e validação de valor vazio. O critério de número máximo de toques ainda contém um espaço para definição da equipe. Registrar o limite escolhido e medir o fluxo. Documentar também persistência de gasto e categoria após reabrir. A implementação ativa chama o DAO diretamente; a tarefa diz ViewModel/DAO, portanto não descrever a integração como uso efetivo de ViewModel sem ajuste ou esclarecimento.

### História 6 — Visualização de limite disponível

Limite editável, saldo calculado e barra visível no Resumo existem. Faltam definir o período diário ou semanal, calcular apenas gastos desse período e representar disponibilidade na barra. O código atual calcula totalGasto/limite: a barra cresce ao adicionar gasto. O critério exige que ela diminua. A barra de disponibilidade deve representar saldo/limite, limitada visualmente entre 0 e 1. Registrar teste manual com valores conhecidos. Com limite de 1000 e gastos de 150, o saldo esperado é 850 e a barra de disponibilidade é 85%; a barra atual de consumo é 15%.

### História 9 — Armazenamento 100% local

Despesas usam Room e o Manifest não declara INTERNET. Porém, o limite usa SharedPreferences, divergindo da tarefa que exige Room como único armazenamento. Registrar teste em modo avião e persistência após reabrir. A apresentação deve separar funcionamento offline de conformidade com a escolha de armazenamento.

### Relação com os quatro itens do plano

Categorias são parte da história 5; limite e barra são parte da história 6; armazenamento local está ligado à história 9; logo está ligada à história 1, já registrada como concluída na iteração 1. A logo não deve ser contada como uma nova história concluída nesta iteração. Os slides de itens planejados ainda devem repetir o plano original e explicar essa correspondência.

### Texto revisado para itens entregues

Funcionalidades implementadas: cadastro com categoria; definição e alteração de limite persistido localmente; cálculo do saldo e barra de consumo; ícones personalizados configurados. Histórias 5, 6 e 9 com critérios pendentes: contagem de toques e validação registrada; período diário/semanal e barra de disponibilidade; persistência do limite no Room e teste offline.

### Atualização recomendada do backlog

Preservar os IDs 1–4 como Feito/SIM conforme o documento. Para os IDs 5, 6 e 9, registrar Em andamento ou Implementado parcialmente, se esses status forem usados pela equipe. Definir responsáveis por implementação e aceite. Após fechar os critérios e obter avaliação, atualizar conclusão e aceite separadamente.
