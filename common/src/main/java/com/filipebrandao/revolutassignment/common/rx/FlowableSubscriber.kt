package com.filipebrandao.revolutassignment.common.rx

import androidx.annotation.NonNull
import com.filipebrandao.revolutassignment.common.logging.ILogger
import io.reactivex.subscribers.ResourceSubscriber
import org.koin.core.KoinComponent
import org.koin.core.inject

open class FlowableSubscriber<T> : ResourceSubscriber<T>(), KoinComponent {

    companion object {
        private const val TAG = "FlowableSubscriber"
    }

    private val logger: ILogger by inject()

    override fun onNext(@NonNull it: T) {
        logger.v(TAG, "onNext")
    }

    override fun onComplete() {
        logger.v(TAG, "Completed")
    }

    override fun onError(e: Throwable) {
        logger.e(TAG, e)
    }
}
