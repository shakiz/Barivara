package com.shakil.barivara.domain.generatebill

import com.shakil.barivara.data.model.BaseApiResponse
import com.shakil.barivara.data.model.generatebill.BillHistoryBaseResponse
import com.shakil.barivara.data.model.generatebill.GenerateBillResponse
import com.shakil.barivara.utils.Resource

interface GenerateBillRepo {
    suspend fun generateBill(month: Int, year: Int): Resource<GenerateBillResponse>
    suspend fun updateBillStatus(billId: Int): Resource<BaseApiResponse>
    suspend fun getBillHistory(): Resource<BillHistoryBaseResponse>
}
