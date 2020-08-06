package com.filipebrandao.revolutassignment.utils.edittext

import android.text.InputFilter
import android.text.InputType
import android.text.Spanned

/**
 * Limits the text input on the EditTexts to a certain amount of decimal digits.
 * Make sure that the EditText has [InputType.TYPE_NUMBER_FLAG_DECIMAL]
 */
class MaximumDecimalDigitsInputFilter(private val maximumDecimalDigits: Int) : InputFilter {

    companion object {
        private const val DECIMAL_DIVIDER = "."
    }

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        dest?.let {
            val dotPosition = dest.indexOf(DECIMAL_DIVIDER)
            if (dotPosition >= 0 && dend > dotPosition && dest.length - dotPosition > maximumDecimalDigits) {
                // the text is entered after the dot and the maximum decimal digits was reached
                return ""
            }
        }

        return null
    }
}
