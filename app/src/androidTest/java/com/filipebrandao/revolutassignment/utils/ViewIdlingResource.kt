package com.filipebrandao.revolutassignment.utils

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.ViewFinder
import org.hamcrest.Matcher
import java.lang.reflect.Field

/**
 * https://stackoverflow.com/a/60458739
 * @param viewMatcher The matcher to find the view.
 * @param idleMatcher The matcher condition to be fulfilled to be considered idle.
 */
class ViewIdlingResource(
    private val viewMatcher: Matcher<View?>?,
    private val idleMatcher: Matcher<View?>?
) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    /**
     * {@inheritDoc}
     */
    override fun isIdleNow(): Boolean {
        val view: View? = getView(viewMatcher)
        val isIdle: Boolean = idleMatcher?.matches(view) ?: false
        if (isIdle) {
            resourceCallback?.onTransitionToIdle()
        }
        return isIdle
    }

    /**
     * {@inheritDoc}
     */
    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = resourceCallback
    }

    /**
     * {@inheritDoc}
     */
    override fun getName(): String? {
        return "$this $viewMatcher"
    }

    /**
     * Tries to find the view associated with the given [<].
     */
    private fun getView(viewMatcher: Matcher<View?>?): View? {
        return try {
            val viewInteraction = onView(viewMatcher)
            val finderField: Field? = viewInteraction.javaClass.getDeclaredField("viewFinder")
            finderField?.isAccessible = true
            val finder = finderField?.get(viewInteraction) as ViewFinder
            finder.view
        } catch (e: Exception) {
            null
        }
    }
}
