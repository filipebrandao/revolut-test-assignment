package com.filipebrandao.revolutassignment.api.services

import com.filipebrandao.revolutassignment.api.endpoints.IRevolutRetrofitServiceDefinition
import com.filipebrandao.revolutassignment.api.httpclients.IHttpClientFactory
import com.filipebrandao.revolutassignment.api.models.RatesDTO
import io.reactivex.Single

class RemoteRevolutService(httpClientFactory: IHttpClientFactory<IRevolutRetrofitServiceDefinition>) : IRevolutService {

    private val api: IRevolutRetrofitServiceDefinition by lazy { httpClientFactory.create() }

    override fun getRates(baseCurrency: String): Single<RatesDTO> = api.getRates(baseCurrency)
}
