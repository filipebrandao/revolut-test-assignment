package com.filipebrandao.revolutassignment.utils

import android.view.View
import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.linkedin.android.testbutler.TestButler
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsNot.not

object TestUtils {

    fun actionOnChildView(id: Int, viewAction: ViewAction): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                viewAction.perform(uiController, v)
            }
        }
    }

    fun getElementFromMatchAtPosition(matcher: Matcher<View?>, position: Int): Matcher<View> {
        return object : BaseMatcher<View>() {
            var counter = 0
            override fun matches(item: Any): Boolean {
                if (matcher.matches(item)) {
                    if (counter == position) {
                        counter++
                        return true
                    }
                    counter++
                }
                return false
            }

            override fun describeTo(description: Description) {
                description.appendText("Element at hierarchy position $position")
            }
        }
    }

    fun isEditTextValueEqualTo(content: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            private var view: EditText? = null

            override fun describeTo(description: Description) {
                if (view == null) {
                    description.appendText("Match Edit Text value ( $content )")
                } else {
                    description.appendText("Match Edit Text value ( $content = ${view!!.text})")
                }
            }

            override fun matchesSafely(view: View): Boolean {
                if (view !is EditText) {
                    return false
                }
                this.view = view
                return view.text.toString().equals(content, ignoreCase = true)
            }
        }
    }

    /**
     * Waits for a matching View to be displayed or throws an error if it's taking too long.
     */
    fun waitUntilViewIsDisplayed(viewMatcher: Matcher<View?>) {
        waitUntil(viewMatcher, isDisplayed())
    }

    /**
     * Waits for a matching View to be hidden or throws an error if it's taking too long.
     */
    fun waitUntilViewIsHidden(viewMatcher: Matcher<View?>) {
        waitUntil(viewMatcher, not(isDisplayed()))
    }

    /**
     * Waits for a matching View or throws an error if it's taking too long.
     */
    private fun waitUntil(viewMatcher: Matcher<View?>, waitFor: Matcher<View?>) {
        val idlingResource: IdlingResource = ViewIdlingResource(viewMatcher, waitFor)
        try {
            IdlingRegistry.getInstance().register(idlingResource)
            // First call to onView is to trigger the idler.
            onView(withId(0)).check(doesNotExist())
        } finally {
            IdlingRegistry.getInstance().unregister(idlingResource)
        }
    }

    /**
     * Changes the wifi and gsm networks state
     */
    fun changeNetworkState(isConnected: Boolean) {
        TestButler.setWifiState(isConnected)
        TestButler.setGsmState(isConnected)
    }
}
