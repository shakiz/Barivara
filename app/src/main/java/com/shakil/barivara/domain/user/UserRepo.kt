package com.shakil.barivara.domain.user

import com.shakil.barivara.data.model.user.UserInfo
import com.shakil.barivara.utils.Resource

interface UserRepo {
    suspend fun getProfile(token: String): Resource<UserInfo>
}
