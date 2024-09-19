package com.shakil.barivara.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakil.barivara.data.repository.AuthRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepoImpl: AuthRepoImpl) : ViewModel() {
    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = authRepoImpl.getAuthData()
        }
    }
}