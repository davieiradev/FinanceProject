package br.edu.ifal.financeproject;

import retrofit2.http.GET
import retrofit2.http.Query
interface BcbApiService {

    @GET("dados/serie/bcbdata.sgs.432/dados/ultimos/1")
    suspend fun getUltimaSelic(
        @Query("formato") formato: String = "json"
    ): List<DadoEconomico>
}
