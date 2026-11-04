package br.edu.ifal.financeproject.model

import com.google.gson.annotations.SerializedName

data class DadoEconomico (
    @SerializedName("br/edu/ifal/financeproject/data")
    val data: String,

    @SerializedName("valor")
    val valor: String
)