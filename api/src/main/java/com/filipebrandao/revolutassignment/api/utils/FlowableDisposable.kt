package com.filipebrandao.revolutassignment.api.utils

import com.filipebrandao.revolutassignment.common.logging.ILogger
import com.filipebrandao.revolutassignment.common.rx.FlowableSubscriber
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException

/**
 * Wires the FlowableSubscriber along with our custom Retrofit errors
 */
abstract class FlowableDisposable<T> : FlowableSubscriber<T>(), KoinComponent {

    companion object {
        private const val TAG = "FlowableDisposable"
    }

    private val logger: ILogger by inject()

    override fun onNext(it: T) {
        next(it)
    }

    override fun onError(e: Throwable) {
        if (e.message != null) {
            logger.e(TAG, e.message!!, e)
        } else {
            logger.e(TAG, e)
        }

        if (e is HttpException) {
            val response = e.response()
            error(RetrofitError(response, e))
        } else {
            error(RetrofitError(null, e))
        }
    }

    abstract fun next(it: T)
    abstract fun error(error: RetrofitError)
}
