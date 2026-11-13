package br.edu.ifal.financeproject.api

import retrofit2.http.GET
import retrofit2.http.Query
import br.edu.ifal.financeproject.domain.CotacaoCripto

interface CryptoApiService {

    @GET("api/v3/ticker/24hr")
    suspend fun buscarCotacao(
        @Query("symbol") simbolo: String
    ): CotacaoCripto
}