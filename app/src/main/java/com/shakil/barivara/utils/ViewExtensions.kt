package com.shakil.barivara.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun <T> filterList(
    query: String,
    originalList: List<T>,
    predicate: (T, String) -> Boolean
): List<T> {
    return if (query.isEmpty()) {
        originalList // Return the original list if the query is empty
    } else {
        originalList.filter { predicate(it, query) } // Filter the list based on the predicate
    }
}

fun EditText.textChanges(): Flow<CharSequence> = callbackFlow {
    val textWatcher = object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            trySend(s ?: "")
        }

        override fun afterTextChanged(s: android.text.Editable?) {}
    }
    addTextChangedListener(textWatcher)
    awaitClose { removeTextChangedListener(textWatcher) }
}

fun EditText.moveToPreviousEditText(
    previousEditText: EditText
) {
    this.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
            if (this.text.isEmpty()) {
                previousEditText.requestFocus()
            }
        }
        false
    }
}

fun EditText.moveToNextEditText(
    nextEditText: EditText,
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s?.length == 1) {
                nextEditText.requestFocus()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}