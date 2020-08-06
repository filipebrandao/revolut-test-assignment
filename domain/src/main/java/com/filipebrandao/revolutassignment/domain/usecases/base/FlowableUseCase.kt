package com.filipebrandao.revolutassignment.domain.usecases.base

import com.filipebrandao.revolutassignment.common.rx.FlowableSubscriber
import io.reactivex.Flowable
import io.reactivex.Scheduler

/**
 * Base class for the Use Cases that rely on a [Flowable] RXJava stream
 */
abstract class FlowableUseCase<T, in Parameters>(
    executionScheduler: Scheduler,
    postExecutionScheduler: Scheduler
) : BaseReactiveUseCase(executionScheduler, postExecutionScheduler) {

    /**
     * Builds a [Flowable] which will be used when the current [FlowableUseCase] is executed
     */
    protected abstract fun buildUseCaseFlowable(params: Parameters? = null): Flowable<T>

    /**
     * Executes the current use case
     */
    fun execute(observer: FlowableSubscriber<T>, params: Parameters? = null) {
        addDisposable(
            buildUseCaseFlowable(params)
                .subscribeOn(executionScheduler)
                .observeOn(postExecutionScheduler)
                .subscribeWith(observer)
        )
    }
}
