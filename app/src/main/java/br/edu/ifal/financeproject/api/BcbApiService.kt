package br.edu.ifal.financeproject.api

import br.edu.ifal.financeproject.domain.DadoEconomico
import retrofit2.http.GET

interface BcbApiService {


    @GET("dados/serie/bcdata.sgs.432/dados/ultimos/1?formato=json")
    suspend fun getUltimaSelic(): List<DadoEconomico>

    @GET("dados/serie/bcdata.sgs.433/dados/ultimos/6?formato=json")
    suspend fun getUltimosIpca(): List<DadoEconomico>
}