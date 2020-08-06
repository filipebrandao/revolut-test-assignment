package com.filipebrandao.revolutassignment

import android.app.Application
import com.filipebrandao.revolutassignment.di.DependencyInjectionEnvironment

class AssignmentApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DependencyInjectionEnvironment.init(this)
    }
}
