package com.filipebrandao.revolutassignment.ui.rates.models

sealed class RatesResult {
    data class Success(val rates: List<RateView>) : RatesResult()
    object Error : RatesResult()
}
