package br.edu.ifal.financeproject.domain

data class CotacaoCripto(
    val symbol: String,
    val lastPrice: String,
    val priceChangePercent: String
)