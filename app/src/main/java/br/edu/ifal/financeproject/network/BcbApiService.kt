package br.edu.ifal.financeproject.network

import br.edu.ifal.financeproject.model.DadoEconomico
import retrofit2.http.GET

interface BcbApiService {


    @GET("dados/serie/bcdata.sgs.432/dados/ultimos/1?formato=json")
    suspend fun getUltimaSelic(): List<DadoEconomico>

    @GET("dados/serie/bcdata.sgs.433/dados/ultimos/6?formato=json")
    suspend fun getUltimosIpca(): List<DadoEconomico>
}