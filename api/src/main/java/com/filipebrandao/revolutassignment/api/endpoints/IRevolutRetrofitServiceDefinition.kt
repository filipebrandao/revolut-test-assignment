package com.filipebrandao.revolutassignment.api.endpoints

import com.filipebrandao.revolutassignment.api.models.RatesDTO
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Defines the Retrofit interface for the remote Revolut Service
 */
interface IRevolutRetrofitServiceDefinition {

    companion object {
        private const val ENDPOINT_RATES = "/api/android/latest"
        private const val QUERY_BASE_CURRENCY = "base"
    }

    @GET(ENDPOINT_RATES)
    fun getRates(@Query(QUERY_BASE_CURRENCY) baseCurrency: String): Single<RatesDTO>
}
