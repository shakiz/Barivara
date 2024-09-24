package com.shakil.barivara.domain.dashboard

import com.shakil.barivara.data.model.dashboard.BaseDashboardResponse
import com.shakil.barivara.utils.Resource

interface DashboardRepo {
    suspend fun getDashboardInfo(token: String): Resource<BaseDashboardResponse>
}