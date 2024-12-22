package com.shakil.barivara

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel(), Observable {

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun onCleared() {
        super.onCleared()
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    @Suppress("unused")
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }
}