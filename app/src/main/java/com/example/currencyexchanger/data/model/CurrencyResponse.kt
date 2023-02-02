package com.example.currencyexchanger.data.model

data class CurrencyResponse(
    val base: String,
    val rates: Rates,
)