package com.filipebrandao.revolutassignment

import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import com.linkedin.android.testbutler.TestButler

/**
 * Sets up a Application and TestButler
 */
class UiTestsRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return Instrumentation.newApplication(TestApp::class.java, context)
    }

    override fun onStart() {
        TestButler.setup(targetContext)
        super.onStart()
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        TestButler.teardown(targetContext)
        super.finish(resultCode, results)
    }
}
