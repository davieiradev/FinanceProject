package br.edu.ifal.financeproject

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CryptoRetrofitClient {

    val api: CryptoApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://data-api.binance.vision/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CryptoApiService::class.java)
    }
}