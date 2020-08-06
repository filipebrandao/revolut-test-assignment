package com.filipebrandao.revolutassignment.utils.edittext

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher

/**
 * Removes needless leading zeroes in an EditText
 * Make sure that the EditText has [InputType.TYPE_NUMBER_FLAG_DECIMAL]
 */
class RemoveLeadingZeroesTextWatcher : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            while (s.startsWith('0') && (!s.startsWith("0.") && s.toString() != "0")) {
                s.delete(0, 1)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // no implementation
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // no implementation
    }
}
