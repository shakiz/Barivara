package com.shakil.barivara

/**
 * Created by Mostafa Monowar on 9/24/18.
 * Brain Station 23.
 */
data class SnackBarMessage(var message: String?, var messageType: Int = SNACK_BAR_DEFAULT) {
    companion object {
        const val SNACK_BAR_DEFAULT = 0
        const val SNACK_BAR_NORMAL = 1
        const val SNACK_BAR_SUCCESS = 2
        const val SNACK_BAR_ERROR = 3
    }
}