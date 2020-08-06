package com.filipebrandao.revolutassignment.domain.usecases.base

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent

/**
 * Base class for Use Cases that rely on RXJava
 */
abstract class BaseReactiveUseCase(
    protected val executionScheduler: Scheduler,
    protected val postExecutionScheduler: Scheduler
) : KoinComponent {

    private val disposables = CompositeDisposable()

    /**
     * Disposes all the disposables associated to this Use Case
     */
    fun dispose() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    /**
     * Stores a disposable that should be disposed later by calling [dispose]
     */
    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }
}
