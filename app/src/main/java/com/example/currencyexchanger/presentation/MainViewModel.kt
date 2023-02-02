package com.example.currencyexchanger.presentation

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchanger.data.model.Rates
import com.example.currencyexchanger.repository.MainRepository
import com.example.currencyexchanger.utils.DispatcherProvider
import com.example.currencyexchanger.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel(), LifecycleObserver {

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion
    sealed class CurrencyEvent {
        class Success(val result: String) : CurrencyEvent()
        class Failure(val error: String) : CurrencyEvent()
        object Loading : CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    fun convert(
        amountStr: String,
        fromCurrency: String,
        toCurrency: String
    ) {
        val fromAmount = amountStr.toFloatOrNull()
        if(fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }

        viewModelScope.launch(dispatcher.io) {
            _conversion.value = CurrencyEvent.Loading
            when(val ratesResponse = repository.getRates(fromCurrency)) {
                is Resource.Error -> _conversion.value = CurrencyEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    val rates = ratesResponse.data!!.rates
                    Log.i("JSONRESPONSE", "${ratesResponse.data}")
                    val rate = getRateForCurrency(toCurrency, rates)
                    if(rate == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        val convertedCurrency = (fromAmount * rate * 100) / 100
                        _conversion.value = CurrencyEvent.Success(
                            "$convertedCurrency"
                        )
                    }
                }
            }
        }
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
        "RUB" -> rates.RUB
        "AED" -> rates.AED
        "AMD" -> rates.AMD
        "AUD" -> rates.AUD
        "AZN" -> rates.AZN
        "BGN" -> rates.BGN
        "BRL" -> rates.BRL
        "BYN" -> rates.BYN
        "CAD" -> rates.CAD
        "CHF" -> rates.CHF
        "CZK" -> rates.CZK
        "DKK" -> rates.DKK
        "EGP" -> rates.EGP
        "EUR" -> rates.EUR
        "GBP" -> rates.GBP
        "GEL" -> rates.GEL
        "HKD" -> rates.HKD
        "HUF" -> rates.HUF
        "IDR" -> rates.IDR
        "INR" -> rates.INR
        "JPY" -> rates.JPY
        "KGS" -> rates.KGS
        "KRW" -> rates.KRW
        "KZT" -> rates.KZT
        "MDL" -> rates.MDL
        "NOK" -> rates.NOK
        "NZD" -> rates.NZD
        "PLN" -> rates.PLN
        "QAR" -> rates.QAR
        "RON" -> rates.RON
        "RSD" -> rates.RSD
        "SEK" -> rates.SEK
        "THB" -> rates.THB
        "TJS" -> rates.TJS
        "TMT" -> rates.TMT
        "TRY" -> rates.TRY
        "UAH" -> rates.UAH
        "USD" -> rates.USD
        "UZS" -> rates.UZS
        "VND" -> rates.VND
        "XDR" -> rates.XDR
        "ZAR" -> rates.ZAR
        else -> null
    }
}