package com.shakil.barivara.data.model.room

import com.google.gson.annotations.SerializedName
import com.shakil.barivara.data.model.BaseApiResponse

data class BaseRoomResponse(
    @SerializedName("data") var data: ArrayList<Room> = arrayListOf()
) : BaseApiResponse()
