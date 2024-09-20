package com.shakil.barivara.domain.generatebill

import com.shakil.barivara.data.model.generatebill.GenerateBillResponse
import com.shakil.barivara.utils.Resource

interface GenerateBillRepo {
    suspend fun generateBill(token: String, month: Int, year: Int): Resource<GenerateBillResponse>
}
