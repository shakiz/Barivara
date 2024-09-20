package com.shakil.barivara.presentation.generatebill

import androidx.lifecycle.ViewModel
import com.shakil.barivara.data.repository.GenerateBillRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenerateBillViewModel @Inject constructor(private val generalBillRepo: GenerateBillRepoImpl) :
    ViewModel() {

}