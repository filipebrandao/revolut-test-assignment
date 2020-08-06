package com.filipebrandao.revolutassignment.api.services

import com.filipebrandao.revolutassignment.api.models.RatesDTO
import io.reactivex.Single

/**
 * Interface for the remote Revolut service
 */
interface IRevolutService {

    /**
     * Get rates
     */
    fun getRates(baseCurrency: String): Single<RatesDTO>
}
