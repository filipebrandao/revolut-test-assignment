package com.filipebrandao.revolutassignment.utils.edittext

import android.text.InputFilter
import android.text.Spanned

/**
 * Limits the text input on the EditTexts to a certain amount of digits
 */
class MaximumDigitsInputFilter(private val maximumDigits: Int) : InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        dest?.let {
            if (it.length >= maximumDigits) {
                return@filter ""
            }
        }
        return null
    }
}
