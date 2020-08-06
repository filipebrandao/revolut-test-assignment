package com.filipebrandao.revolutassignment.api.httpclients

import com.filipebrandao.revolutassignment.api.IExternalValues
import com.filipebrandao.revolutassignment.api.endpoints.IRevolutRetrofitServiceDefinition
import com.filipebrandao.revolutassignment.common.logging.ILogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Factory for the service httpclient
 */
class HttpClientFactory(private val externalValues: IExternalValues) : IHttpClientFactory<IRevolutRetrofitServiceDefinition>, KoinComponent {

    companion object {
        private const val TAG = "HttpClientFactory"
        private const val OK_HTTP_LOG_TAG = "OkHttp"
    }

    private val logger: ILogger by inject()

    override fun create(): IRevolutRetrofitServiceDefinition {
        logger.d(TAG, "Will create HttpClientFactory")

        val httpClient = OkHttpClient.Builder()

        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                // log through our own Logger
                logger.d(OK_HTTP_LOG_TAG, message)
            }
        }).apply {
            this.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(this)
        }

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(externalValues.getBaseUrl())
            .client(httpClient.build()).build()

        return retrofit.create(IRevolutRetrofitServiceDefinition::class.java)
    }
}
