package com.example.currencyexchanger.data.model

data class CurrencyResponse(
    val base: String,
    val date: String,
    val disclaimer: String,
    val rates: Rates,
    val timestamp: Int
)