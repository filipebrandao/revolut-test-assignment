package com.filipebrandao.revolutassignment

import android.view.KeyEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.filipebrandao.revolutassignment.api.models.RatesDTO
import com.filipebrandao.revolutassignment.api.services.IRevolutService
import com.filipebrandao.revolutassignment.di.DependencyInjectionEnvironment
import com.filipebrandao.revolutassignment.ui.rates.RatesActivity
import com.filipebrandao.revolutassignment.ui.rates.RatesListAdapter
import com.filipebrandao.revolutassignment.utils.TestUtils
import com.filipebrandao.revolutassignment.utils.TestUtils.getElementFromMatchAtPosition
import com.filipebrandao.revolutassignment.utils.TestUtils.isEditTextValueEqualTo
import io.reactivex.Single
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.net.SocketException
import java.text.DecimalFormat

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RatesActivityTest {

    companion object {
        const val BASE_CURRENCY_NAME = "EUR"
        const val SECOND_CURRENCY_NAME = "PLN"
        const val THIRD_CURRENCY_NAME = "HUF"
        const val FOURTH_CURRENCY_NAME = "ILS"

        const val SECOND_CURRENCY_RATE = 1.0
        const val THIRD_CURRENCY_RATE = 2.0
        const val FOURTH_CURRENCY_RATE = 0.5

        // Flag that controls wether the network requests are successful or not
        var networkRequestSuccess = true
    }

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(RatesActivity::class.java, false, false)

    @Before
    fun setup() {
        // get the original koin di environment definition
        val koinModules = ArrayList(DependencyInjectionEnvironment.getModules())

        // define mocks
        val remoteServiceMock = object : IRevolutService {
            override fun getRates(baseCurrency: String): Single<RatesDTO> {
                // remote service will return fake currencies
                return if (networkRequestSuccess) {
                    Single.just(
                        RatesDTO(
                            BASE_CURRENCY_NAME,
                            mapOf(SECOND_CURRENCY_NAME to SECOND_CURRENCY_RATE, THIRD_CURRENCY_NAME to THIRD_CURRENCY_RATE, FOURTH_CURRENCY_NAME to FOURTH_CURRENCY_RATE)
                        )
                    )
                } else {
                    Single.error(SocketException())
                }
            }
        }

        // override di environment with mocks
        val overridesModule = module(override = true) {
            single<IRevolutService>(override = true) { remoteServiceMock }
        }
        koinModules.add(overridesModule)

        // setup koin di environment
        loadKoinModules(koinModules)

        Intents.init()
    }

    @After
    fun cleanup() {
        Intents.release()
        networkRequestSuccess = true
    }

    @Test
    fun checkInitialState() {
        // GIVEN
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))

        // THEN
        // assert that the currencies list is visible and all list items are showing the 0 hint value
        onView(withId(R.id.list))
            .check(matches(hasDescendant(allOf(withId(R.id.valueView), withHint("0")))))
    }

    @Test
    fun maximumDigitsIsNine() {
        // GIVEN
        val NINE_DIGIT_NUMBER = "123456789"
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))
        // write a number with 10 digits
        onView(withId(R.id.list))
            .perform(executeActionOnFirstCurrencyEditText(typeText(NINE_DIGIT_NUMBER)))
            .perform(executeActionOnFirstCurrencyEditText(typeText("1")))

        // THEN
        // assert only 9 digits were considered
        assertOnFirstCurrencyEditText(matches(isEditTextValueEqualTo(NINE_DIGIT_NUMBER)))
    }

    @Test
    fun maximumDecimalDigitsIsTwo() {
        // GIVEN
        val NUMBER_WITH_MAXIMUM_DECIMAL_DIGITS = "123.45"
        val NUMBER_WITH_MORE_THAN_MAXIMUM_DECIMAL_DIGITS = NUMBER_WITH_MAXIMUM_DECIMAL_DIGITS + "1"
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))
        // write a number with 3 decimal digits
        onView(withId(R.id.list))
            .perform(executeActionOnFirstCurrencyEditText(typeText(NUMBER_WITH_MORE_THAN_MAXIMUM_DECIMAL_DIGITS)))

        // THEN
        // assert only 2 decimal digits were considered
        assertOnFirstCurrencyEditText(matches(isEditTextValueEqualTo(NUMBER_WITH_MAXIMUM_DECIMAL_DIGITS)))
    }

    @Test
    fun inputtingLeadingZeroesAreIgnored() {
        // GIVEN
        val INPUT = "001001"
        val INPUT_AFTER_DELETING_FIRST_CHAR = "1001"
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))
        // write "1001", move the cursor to after the first character and delete it
        onView(withId(R.id.list))
            .perform(executeActionOnFirstCurrencyEditText(typeText(INPUT)))

        // THEN
        // assert that the first two zeroes were ignored
        assertOnFirstCurrencyEditText(matches(isEditTextValueEqualTo(INPUT_AFTER_DELETING_FIRST_CHAR)))

        // THEN
        // assert that the first two zeroes were ignored along with the other two zeroes after deleting
        assertOnFirstCurrencyEditText(matches(isEditTextValueEqualTo(INPUT_AFTER_DELETING_FIRST_CHAR)))
    }

    @Test
    fun strippingOutLeadingZeroes() {
        // GIVEN
        val INPUT = "001001"
        val INPUT_AFTER_DELETING_FIRST_CHAR = "1"
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))
        // write "1001", move the cursor to after the first character and delete it
        onView(withId(R.id.list))
            .perform(executeActionOnFirstCurrencyEditText(typeText(INPUT)))
            .perform(executeActionOnFirstCurrencyEditText(pressKey(KeyEvent.KEYCODE_DPAD_LEFT)))
            .perform(executeActionOnFirstCurrencyEditText(pressKey(KeyEvent.KEYCODE_DPAD_LEFT)))
            .perform(executeActionOnFirstCurrencyEditText(pressKey(KeyEvent.KEYCODE_DPAD_LEFT)))
            .perform(executeActionOnFirstCurrencyEditText(pressKey(KeyEvent.KEYCODE_DEL)))

        // THEN
        // assert all the zeroes were stripped out
        assertOnFirstCurrencyEditText(matches(isEditTextValueEqualTo(INPUT_AFTER_DELETING_FIRST_CHAR)))
    }

    @Test
    fun changingValueUpdatesOtherCurrenciesAccordingly() {
        // GIVEN
        val decimalFormat = DecimalFormat("#.##")
        val INPUT = 100
        val EXPECTED_INPUT_SECOND_CURRENCY = decimalFormat.format(INPUT * SECOND_CURRENCY_RATE)
        val EXPECTED_INPUT_THIRD_CURRENCY = decimalFormat.format(INPUT * THIRD_CURRENCY_RATE)
        val EXPECTED_INPUT_FOURTH_CURRENCY = decimalFormat.format(INPUT * FOURTH_CURRENCY_RATE)
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))
        // write "100" in the active currency value
        onView(withId(R.id.list))
            .perform(executeActionOnFirstCurrencyEditText(typeText("$INPUT")))

        // THEN
        // assert all the other currencies values were updated
        assertOnNthCurrencyEditText(matches(isEditTextValueEqualTo(EXPECTED_INPUT_SECOND_CURRENCY)), 1)
        assertOnNthCurrencyEditText(matches(isEditTextValueEqualTo(EXPECTED_INPUT_THIRD_CURRENCY)), 2)
        assertOnNthCurrencyEditText(matches(isEditTextValueEqualTo(EXPECTED_INPUT_FOURTH_CURRENCY)), 3)
    }

    @Test
    fun selectingOtherCurrencyMakesItTheActiveOne() {
        // GIVEN
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))
        // click the second currency to make it the active one
        onView(withId(R.id.list))
            .perform(executeActionOnNthCurrencyEditText(click(), 1))

        // THEN
        // assert that it moved to the top
        assertOnNthView(R.id.titleView, 0, matches(withText(SECOND_CURRENCY_NAME)))
    }

    @Test
    fun inputIsRestrictedToNumericDigits() {
        // GIVEN
        val INPUT_WITH_NON_NUMERIC_DIGITS = "ab1cd2ef3"
        val EXPECTED_INPUT = "123"
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))
        // write non numeric digits along with numeric digits
        onView(withId(R.id.list))
            .perform(executeActionOnFirstCurrencyEditText(typeText(INPUT_WITH_NON_NUMERIC_DIGITS)))

        // THEN
        // assert that only the numeric digits were allowed
        assertOnFirstCurrencyEditText(matches(isEditTextValueEqualTo(EXPECTED_INPUT)))
    }

    @Test
    fun networkLostShowsWarning() {
        // GIVEN
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))
        // network connection goes off
        TestUtils.changeNetworkState(false)

        // THEN
        // no network warning is displayed
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.noNetworkView))
        onView(withId(R.id.noNetworkView)).check(matches(isDisplayed()))

        // WHEN
        // network connection set to on
        TestUtils.changeNetworkState(true)

        // THEN
        // no network warning is hidden
        TestUtils.waitUntilViewIsHidden(withId(R.id.noNetworkView))
        onView(withId(R.id.noNetworkView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun errorScreenShownWhenFirstRemoteCallFails() {
        // GIVEN
        // network request will fail
        networkRequestSuccess = false
        activityRule.launchActivity(null)

        // WHEN
        waitForActivity()

        // THEN
        // error screen is shown
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.errorTextView))
        onView(withId(R.id.errorTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.retryButton)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check(matches(not(isDisplayed())))

        // WHEN
        // network request will be successful WHEN pressing retry
        networkRequestSuccess = true
        onView(withId(R.id.retryButton)).perform(click())

        // THEN
        // error screen hides and the currencies screen gets visible
        TestUtils.waitUntilViewIsDisplayed(withId(R.id.list))
        onView(withId(R.id.errorTextView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.retryButton)).check(matches(not(isDisplayed())))
        onView(withId(R.id.list)).check(matches(isDisplayed()))
    }

    private fun waitForActivity() {
        waitForActivity()
    }
    private fun executeActionOnFirstCurrencyEditText(viewAction: ViewAction): ViewAction {
        return executeActionOnNthCurrencyEditText(viewAction, 0)
    }

    private fun executeActionOnNthCurrencyEditText(viewAction: ViewAction, n: Int): ViewAction {
        return executeActionOnNthView(R.id.valueView, n, viewAction)
    }

    private fun executeActionOnNthView(viewId: Int, n: Int, viewAction: ViewAction): ViewAction {
        return actionOnItemAtPosition<RatesListAdapter.RateViewHolder>(n, TestUtils.actionOnChildView(viewId, viewAction))
    }

    private fun assertOnFirstCurrencyEditText(assertion: ViewAssertion) {
        onView(getElementFromMatchAtPosition(withId(R.id.valueView), 0)).check(assertion)
    }

    private fun assertOnNthCurrencyEditText(assertion: ViewAssertion, n: Int) {
        return assertOnNthView(R.id.valueView, n, assertion)
    }

    private fun assertOnNthView(viewId: Int, n: Int, assertion: ViewAssertion) {
        onView(getElementFromMatchAtPosition(withId(viewId), n)).check(assertion)
    }
}
