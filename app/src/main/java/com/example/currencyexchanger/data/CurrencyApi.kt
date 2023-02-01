package com.example.currencyexchanger.data

import com.example.currencyexchanger.data.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("/latest.js")
    suspend fun getRates(
        @Query("base") base: String
    ): Response<CurrencyResponse>
}