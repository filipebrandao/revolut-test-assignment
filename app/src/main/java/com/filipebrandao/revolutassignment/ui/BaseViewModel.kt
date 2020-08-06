package com.filipebrandao.revolutassignment.ui

import androidx.lifecycle.ViewModel
import com.filipebrandao.revolutassignment.common.logging.ILogger
import com.filipebrandao.revolutassignment.domain.usecases.base.BaseReactiveUseCase
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject

open class BaseViewModel : ViewModel(), KoinComponent {
    protected val logger: ILogger by inject()

    // generic usage disposable, since most viewmodels extending this will likely subscribe to one rx stream
    private val disposable = CompositeDisposable()

    // tracks executed [BaseReactiveUseCase]'s in order to dispose them
    private val executedReactiveUseCases = mutableListOf<BaseReactiveUseCase>()

    protected fun addDisposable(useCase: BaseReactiveUseCase) {
        executedReactiveUseCases.add(useCase)
    }

    override fun onCleared() {
        super.onCleared()
        logger.v(javaClass.simpleName, "onCleared")

        disposable.clear()

        disposeReactiveUseCases()
    }

    private fun disposeReactiveUseCases() {
        executedReactiveUseCases.iterator().let { iterator ->
            while (iterator.hasNext()) {
                iterator.next().let { useCase ->
                    useCase.dispose()
                    iterator.remove()
                }
            }
        }
    }
}
