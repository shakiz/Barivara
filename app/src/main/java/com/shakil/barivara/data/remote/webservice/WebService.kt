package com.shakil.barivara.data.remote.webservice

import com.shakil.barivara.data.remote.dto.GetDataDto
import retrofit2.Response
import retrofit2.http.GET

interface WebService {
    @GET("endpoint")
    suspend fun getData(): Response<GetDataDto>
}
