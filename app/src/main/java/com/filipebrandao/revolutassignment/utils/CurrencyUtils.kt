package com.filipebrandao.revolutassignment.utils

import android.content.Context
import com.filipebrandao.revolutassignment.R

class CurrencyUtils(context: Context) {

    private val currencyNames = mutableMapOf<String, String>()

    init {
        // fill the map of currency names with localized strings
        currencyNames["EUR"] = context.getString(R.string.currency_eur)
        currencyNames["AUD"] = context.getString(R.string.currency_aud)
        currencyNames["BGN"] = context.getString(R.string.currency_bgn)
        currencyNames["BRL"] = context.getString(R.string.currency_brl)
        currencyNames["CAD"] = context.getString(R.string.currency_cad)
        currencyNames["CHF"] = context.getString(R.string.currency_chf)
        currencyNames["CNY"] = context.getString(R.string.currency_cny)
        currencyNames["CZK"] = context.getString(R.string.currency_czk)
        currencyNames["DKK"] = context.getString(R.string.currency_dkk)
        currencyNames["GBP"] = context.getString(R.string.currency_gbp)
        currencyNames["HKD"] = context.getString(R.string.currency_hkd)
        currencyNames["HRK"] = context.getString(R.string.currency_hrk)
        currencyNames["HUF"] = context.getString(R.string.currency_huf)
        currencyNames["IDR"] = context.getString(R.string.currency_idr)
        currencyNames["ILS"] = context.getString(R.string.currency_ils)
        currencyNames["INR"] = context.getString(R.string.currency_inr)
        currencyNames["ISK"] = context.getString(R.string.currency_isk)
        currencyNames["JPY"] = context.getString(R.string.currency_jpy)
        currencyNames["KRW"] = context.getString(R.string.currency_krw)
        currencyNames["MXN"] = context.getString(R.string.currency_mxn)
        currencyNames["MYR"] = context.getString(R.string.currency_myr)
        currencyNames["NOK"] = context.getString(R.string.currency_nok)
        currencyNames["NZD"] = context.getString(R.string.currency_nzd)
        currencyNames["SGD"] = context.getString(R.string.currency_sgd)
        currencyNames["PHP"] = context.getString(R.string.currency_php)
        currencyNames["PLN"] = context.getString(R.string.currency_pln)
        currencyNames["RON"] = context.getString(R.string.currency_ron)
        currencyNames["RUB"] = context.getString(R.string.currency_rub)
        currencyNames["SEK"] = context.getString(R.string.currency_sek)
        currencyNames["THB"] = context.getString(R.string.currency_thb)
        currencyNames["USD"] = context.getString(R.string.currency_usd)
        currencyNames["ZAR"] = context.getString(R.string.currency_zar)
    }

    /**
     * Retrieves the name of a currency based on its short name
     */
    fun getCurrencyName(shortName: String): String? {
        return currencyNames[shortName]
    }
}
