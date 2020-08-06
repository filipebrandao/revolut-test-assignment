package com.filipebrandao.revolutassignment.domain.usecases

import com.filipebrandao.revolutassignment.api.models.RatesDTO
import com.filipebrandao.revolutassignment.api.services.IRevolutService
import com.filipebrandao.revolutassignment.domain.models.Currency
import com.filipebrandao.revolutassignment.domain.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import java.util.concurrent.TimeUnit

/**
 * Fetches the rates for all currencies using Euro as the base currency, updating every second
 */
class FetchRatesUseCase(
    private val remoteService: IRevolutService,
    executionScheduler: Scheduler,
    postExecutionScheduler: Scheduler
) : FlowableUseCase<List<Currency>, Nothing>(executionScheduler, postExecutionScheduler) {

    companion object {
        private const val EURO_CURRENCY = "EUR"
    }

    override fun buildUseCaseFlowable(params: Nothing?): Flowable<List<Currency>> {
        return Flowable.interval(1, TimeUnit.SECONDS)
            .flatMapSingle { remoteService.getRates(EURO_CURRENCY) }
            .map { it.mapToCurrency() }
    }

    private fun RatesDTO.mapToCurrency(): List<Currency> {
        return mutableListOf(Currency(EURO_CURRENCY, 1.0))
            .also { currenciesList ->
                rates.forEach { currenciesList.add(Currency(it.key, it.value)) }
            }
    }
}
