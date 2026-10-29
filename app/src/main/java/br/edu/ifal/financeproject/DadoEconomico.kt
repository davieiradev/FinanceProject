package br.edu.ifal.financeproject

import com.google.gson.annotations.SerializedName

data class DadoEconomico (
    @SerializedName("data")
    val data: String,

    @SerializedName("valor")
    val valor: String
)
