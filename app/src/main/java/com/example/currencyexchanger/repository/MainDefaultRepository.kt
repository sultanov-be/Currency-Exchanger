package com.example.currencyexchanger.repository

import com.example.currencyexchanger.data.CurrencyApi
import com.example.currencyexchanger.data.model.CurrencyResponse
import com.example.currencyexchanger.utils.Resource
import javax.inject.Inject

class MainDefaultRepository @Inject constructor(
    private val api: CurrencyApi) : MainRepository {

    override suspend fun getRates(base: String): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates(base)
            val result = response.body()
            if(response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch(e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}