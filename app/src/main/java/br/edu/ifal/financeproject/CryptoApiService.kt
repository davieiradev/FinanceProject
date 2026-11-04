package br.edu.ifal.financeproject

import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoApiService {

    @GET("api/v3/ticker/24hr")
    suspend fun buscarCotacao(
        @Query("symbol") simbolo: String
    ): CotacaoCripto
}