package com.shakil.barivara.domain.auth

import com.shakil.barivara.data.model.User
import com.shakil.barivara.utils.Resource

interface AuthRepo {
    suspend fun getAuthData(): Resource<User>
}