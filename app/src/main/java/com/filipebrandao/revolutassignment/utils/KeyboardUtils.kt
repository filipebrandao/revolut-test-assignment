package com.filipebrandao.revolutassignment.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardUtils(context: Context) {

    private val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?

    fun hideKeyboard(view: View) {
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
