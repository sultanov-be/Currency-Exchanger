package com.example.currencyexchanger.repository

import com.example.currencyexchanger.data.model.CurrencyResponse
import com.example.currencyexchanger.utils.Resource

interface MainRepository {
    suspend fun getRates(base: String): Resource<CurrencyResponse>
}