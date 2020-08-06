package com.filipebrandao.revolutassignment.di

import android.content.Context
import com.filipebrandao.revolutassignment.api.ExternalValues
import com.filipebrandao.revolutassignment.api.httpclients.HttpClientFactory
import com.filipebrandao.revolutassignment.api.services.IRevolutService
import com.filipebrandao.revolutassignment.api.services.RemoteRevolutService
import com.filipebrandao.revolutassignment.common.logging.ILogger
import com.filipebrandao.revolutassignment.common.logging.LogCatLogger
import com.filipebrandao.revolutassignment.ui.rates.RatesViewModel
import com.filipebrandao.revolutassignment.utils.CurrencyUtils
import com.filipebrandao.revolutassignment.utils.KeyboardUtils
import com.filipebrandao.revolutassignment.utils.network.NetworkUtils
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Defines the Dependency Injection Environment leveraging on the Koin library.
 */
object DependencyInjectionEnvironment {

    private val ratesModule: Module = module {
        viewModel { RatesViewModel(remoteService = get()) }
    }

    private val remoteApiModule: Module = module {
        single<IRevolutService> { RemoteRevolutService(httpClientFactory = HttpClientFactory(ExternalValues())) }
    }

    private val utilsModule: Module = module {
        single<ILogger> { LogCatLogger() }
        single<NetworkUtils> { NetworkUtils(context = androidContext()) }
        single<KeyboardUtils> { KeyboardUtils(context = androidContext()) }
        single<CurrencyUtils> { CurrencyUtils(context = androidContext()) }
    }

    fun getModules(): List<Module> {
        return listOf(
            ratesModule,
            remoteApiModule,
            utilsModule
        )
    }

    fun init(context: Context): KoinApplication {
        return startKoin {
            androidContext(context)
            modules(getModules())
        }
    }
}
