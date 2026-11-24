package br.edu.ifal.financeproject.financas

data class EvolucaoJuros(
    val mes: Int,
    val jurosDoMes: Double,
    val totalAcumulado: Double
)

data class ResultadoJuros(
    val valorInicial: Double,
    val taxaMensalPercentual: Double,
    val prazoMeses: Int,
    val total: Double,
    val totalJuros: Double,
    val evolucao: List<EvolucaoJuros>
)

fun calcularJurosCompostos(
    valor: Double,
    taxaMensalPercentual: Double,
    prazoMeses: Int
): ResultadoJuros {
    require(valor.isFinite() && valor > 0.0) { "O valor deve ser maior que zero." }
    require(taxaMensalPercentual.isFinite() && taxaMensalPercentual > 0.0 && taxaMensalPercentual <= 20.0) {
        "A taxa deve ser maior que 0% e de até 20% ao mês."
    }
    require(prazoMeses in 1..60) { "O prazo deve ser de 1 a 60 meses." }

    val taxa = taxaMensalPercentual / 100.0
    var acumulado = valor
    val evolucao = (1..prazoMeses).map { mes ->
        val jurosMes = acumulado * taxa
        acumulado += jurosMes
        require(acumulado.isFinite()) { "Valor muito alto para simulação." }
        EvolucaoJuros(mes, jurosMes, acumulado)
    }
    return ResultadoJuros(
        valorInicial = valor,
        taxaMensalPercentual = taxaMensalPercentual,
        prazoMeses = prazoMeses,
        total = acumulado,
        totalJuros = acumulado - valor,
        evolucao = evolucao
    )
}
