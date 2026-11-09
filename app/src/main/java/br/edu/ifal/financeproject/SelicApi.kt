package br.edu.ifal.financeproject

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/** Resposta da série 11 do SGS: taxa Selic efetiva, em percentual ao dia. */
data class TaxaSelicDto(
    @SerializedName("data") val data: String,
    @SerializedName("valor") val valor: String
)

interface BancoCentralApi {
    @GET("dados/serie/bcdata.sgs.11/dados")
    suspend fun buscarSelicDiaria(
        @Query("formato") formato: String = "json",
        @Query("dataInicial") dataInicial: String,
        @Query("dataFinal") dataFinal: String
    ): List<TaxaSelicDto>
}

object RetrofitBancoCentral {
    val api: BancoCentralApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.bcb.gov.br/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BancoCentralApi::class.java)
    }
}
