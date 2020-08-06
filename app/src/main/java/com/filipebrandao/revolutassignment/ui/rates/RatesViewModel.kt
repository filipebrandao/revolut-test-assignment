package com.filipebrandao.revolutassignment.ui.rates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.filipebrandao.revolutassignment.api.services.IRevolutService
import com.filipebrandao.revolutassignment.api.utils.FlowableDisposable
import com.filipebrandao.revolutassignment.api.utils.RetrofitError
import com.filipebrandao.revolutassignment.domain.models.Currency
import com.filipebrandao.revolutassignment.domain.usecases.FetchRatesUseCase
import com.filipebrandao.revolutassignment.ui.BaseViewModel
import com.filipebrandao.revolutassignment.ui.rates.models.RateView
import com.filipebrandao.revolutassignment.ui.rates.models.RatesResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RatesViewModel(private val remoteService: IRevolutService) : BaseViewModel() {

    companion object {
        private const val TAG = "RatesViewModel"
    }

    private val _rates = MutableLiveData<RatesResult>()
    val rates: LiveData<RatesResult> = _rates

    private var fetchRatesUseCase: FetchRatesUseCase? = null

    fun observeRates() {
        logger.d(TAG, "Observing rates")

        // dispose the usecase stream if it's already being subscribed
        fetchRatesUseCase?.dispose()

        // subscribe the usecase to get updated rates every seconds
        fetchRatesUseCase = FetchRatesUseCase(remoteService, Schedulers.single(), AndroidSchedulers.mainThread())

        fetchRatesUseCase!!.execute(object : FlowableDisposable<List<Currency>>() {
            override fun next(it: List<Currency>) {
                logger.d(TAG, "Got %d updated rates", it.size)
                val rates = it.map { RateView(it.currency, it.currency, it.rateToEuro) }
                _rates.postValue(RatesResult.Success(rates))
            }

            override fun error(error: RetrofitError) {
                logger.e(TAG, "Error while fetching the updated rates: %s", error.errorMessage)
                _rates.postValue(RatesResult.Error)
            }
        })

        addDisposable(fetchRatesUseCase!!)
    }
}
