package com.filipebrandao.revolutassignment.ui.rates

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.filipebrandao.revolutassignment.R
import com.filipebrandao.revolutassignment.common.logging.ILogger
import com.filipebrandao.revolutassignment.ui.rates.models.RateView
import com.filipebrandao.revolutassignment.utils.CurrencyUtils
import com.filipebrandao.revolutassignment.utils.KeyboardUtils
import com.filipebrandao.revolutassignment.utils.bindView
import com.filipebrandao.revolutassignment.utils.edittext.MaximumDecimalDigitsInputFilter
import com.filipebrandao.revolutassignment.utils.edittext.MaximumDigitsInputFilter
import com.filipebrandao.revolutassignment.utils.edittext.RemoveLeadingZeroesTextWatcher
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale

class RatesListAdapter(
    activity: Activity,
    private val rates: ArrayList<RateView>,
    private val onItemClickedCallback: () -> (Unit)
) : KoinComponent, RecyclerView.Adapter<RatesListAdapter.RateViewHolder>() {

    companion object {
        private const val TAG = "RateListAdapter"
        private const val IMAGE_URL_CURRENCY_PARAM = "{currency}"
        private const val IMAGE_URL = "https://www.xe.com/themes/xe/images/flags/svg/$IMAGE_URL_CURRENCY_PARAM.svg"
        private const val DIGIT_LIMIT = 9
        private const val DECIMAL_DIGIT_LIMIT = 2

        private val CURRENCY_VALUE_FORMATTER: DecimalFormat = DecimalFormat("#.##")
    }

    private val logger: ILogger by inject()
    private val keyboardUtils: KeyboardUtils by inject()
    private val currencyUtils: CurrencyUtils by inject()

    /**
     * Loads remote SVG images
     */
    private val svgLoader = SvgLoader.pluck().with(activity)

    /**
     * Holds the calculated values for all currencies
     */
    private val currencyValues = ArrayList<BigDecimal>(rates.size)

    /**
     * Click listener to make a currency the active one
     */
    private val onCurrencySelected: (Int) -> Unit = { position ->
        if (position != 0) {
            logger.d(TAG, "User selected currency %s at index %d will be moved to the top", { rates[position].shortName }, position)
            // move selected rate to the top
            val selectedRate = rates.removeAt(position)
            rates.add(0, selectedRate)
            // update the currency values as well
            val selectedCurrency = currencyValues.removeAt(position)
            currencyValues.add(0, selectedCurrency)

            notifyItemMoved(position, 0)
            notifyItemChanged(position)
            notifyItemChanged(0)

            onItemClickedCallback()
        }
    }

    /**
     * Watches for text changes in the active currency EditText and updates the other currency depending on current the rates
     */
    private val activeCurrencyTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                val inputAsDouble = it.toString().toDoubleOrNull()
                if (inputAsDouble == null || inputAsDouble == 0.0) {
                    currencyValues.forEachIndexed { index, _ -> currencyValues[index] = BigDecimal.ZERO }
                } else {
                    currencyValues[0] = BigDecimal(it.toString())
                    recalculateValues()
                }
                notifyItemRangeChanged(1, rates.size - 1)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // no implementation
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // no implementation
        }
    }

    /**
     * Limits the text input on the EditTexts to 9 digits while still allowing it to have bigger values resulting of the rate conversion
     */
    private val nineDigitMaxInputFilter = MaximumDigitsInputFilter(DIGIT_LIMIT)

    /**
     * Limits the text input to numbers with 2 decimal digits
     */
    private val twoDecimalDigitsMaxInputFilter = MaximumDecimalDigitsInputFilter(DECIMAL_DIGIT_LIMIT)

    /**
     * Prevents the text input with needless leading zeroes
     */
    private val preventLeadingZeroesTextWatcher = RemoveLeadingZeroesTextWatcher()

    init {
        // initialize the values for each currency as zero
        rates.forEach { _ ->
            currencyValues += BigDecimal.ZERO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        return RateViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_rates_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return rates.size
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bind(rates[position], position)
    }

    /**
     * Updates the rates
     */
    fun updateRates(updatedRates: List<RateView>) {
        logger.d(TAG, "Updating rates")

        // assuming the updated rates will never consider new currencies, we just update the rates values
        updatedRates.forEach { updatedRate ->
            val rateIndex = rates.indexOfFirst { it.shortName == updatedRate.shortName }
            logger.v(TAG, "Currency ${rates[rateIndex].shortName} went from ${rates[rateIndex].value} to ${updatedRate.value}")
            rates[rateIndex] = updatedRate
        }
        // it's highly likely that all rates have changed
        recalculateValues()
        notifyItemRangeChanged(1, rates.size - 1)
    }

    /**
     * Calculates all currency values depending on the rates
     */
    private fun recalculateValues() {
        logger.d(TAG, "Recalculating values")

        if (currencyValues[0].compareTo(BigDecimal.ZERO) != 0) {
            // calculate the active currency value in euro and then convert all the other currencies, since the rate values are based in the euro currency
            val activeCurrencyConvertedToEuro = currencyValues[0].divide(BigDecimal.valueOf(rates[0].value), MathContext.DECIMAL128)
            logger.v(TAG, "Active currency (%s) value is %.2f which is %.2f in euro", rates[0].shortName, currencyValues[0], activeCurrencyConvertedToEuro)

            currencyValues.drop(1).forEachIndexed { i, _ ->
                val index = i + 1
                currencyValues[index] = activeCurrencyConvertedToEuro * BigDecimal.valueOf(rates[index].value)
                logger.v(TAG, "New value for currency %s is %.2f", rates[index].shortName, currencyValues[index])
            }
        }
    }

    /**
     * Updates the active currency value
     */
    fun setActiveCurrencyValue(value: BigDecimal) {
        currencyValues[0] = value
        recalculateValues()
        notifyDataSetChanged()
    }

    fun getRates(): ArrayList<RateView> {
        return rates
    }

    fun getActiveCurrencyValue(): BigDecimal {
        return currencyValues[0]
    }

    inner class RateViewHolder(row: View) : RecyclerView.ViewHolder(row) {

        private val titleText by bindView<TextView>(R.id.titleView)
        private val subtitleText by bindView<TextView>(R.id.subtitleView)
        private val logoImage by bindView<ImageView>(R.id.logoView)
        private val rateEditText by bindView<EditText>(R.id.valueView)

        fun bind(rate: RateView, position: Int) {
            titleText.text = rate.shortName
            subtitleText.text = currencyUtils.getCurrencyName(rate.shortName) ?: ""

            svgLoader
                .setPlaceHolder(R.drawable.rate_placeholder, R.drawable.rate_placeholder)
                .load(IMAGE_URL.replace(IMAGE_URL_CURRENCY_PARAM, rate.shortName.toLowerCase(Locale.ROOT)), logoImage)

            rateEditText.removeTextChangedListener(activeCurrencyTextWatcher)
            rateEditText.removeTextChangedListener(preventLeadingZeroesTextWatcher)

            if (currencyValues[position].setScale(DECIMAL_DIGIT_LIMIT, RoundingMode.DOWN).compareTo(BigDecimal.ZERO) == 0) {
                rateEditText.setText("")
            } else {
                rateEditText.setText(CURRENCY_VALUE_FORMATTER.format(currencyValues[position]))
            }

            if (position == 0) {
                // enable text change updates for the active currency only
                rateEditText.addTextChangedListener(activeCurrencyTextWatcher)
                rateEditText.addTextChangedListener(preventLeadingZeroesTextWatcher)
                // disable click listeners to make this the active currency
                rateEditText.setOnClickListener(null)
                itemView.setOnClickListener(null)
                // allow click in the currency value EditText to grab focus
                rateEditText.isFocusableInTouchMode = true
                rateEditText.setOnFocusChangeListener { _, hasFocus ->
                    // when scrolling the list, if the soft keyboard is shown, it will switch to a full qwerty keyboard as soon as this edittext looses focus thus we just hide it here.
                    // note that this doesn't work well if there is a small amount of list items (just enough to fill the screen) since the list will loose the ability to scroll while it's being scrolled.
                    // let's keep this logic assume that there will always be a large enough amount of list items.
                    if (!hasFocus) {
                        keyboardUtils.hideKeyboard(rateEditText)
                    }
                }
                // set input sanitizers
                rateEditText.filters = arrayOf(nineDigitMaxInputFilter, twoDecimalDigitsMaxInputFilter)
            } else {
                // allow list items that are not the first to be made the active currency
                rateEditText.setOnClickListener { onCurrencySelected(position) }
                itemView.setOnClickListener { onCurrencySelected(position) }
                // disable click in the edittext to grab focus since it's not editable if it's not the active currency
                rateEditText.isFocusable = false
                // also disable the input sanitizers since it's not the active currency
                rateEditText.filters = arrayOf()
            }
        }
    }
}
