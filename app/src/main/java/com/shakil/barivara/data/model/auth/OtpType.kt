package com.shakil.barivara.data.model.auth

enum class OtpType(val value: String) {
    OTP("otp"),
    SET_PASS("setpass"),
    OTHERS("others");

    companion object {
        fun from(value: String): OtpType? {
            return entries.find { it.value == value }
        }
    }

    fun toValue(): String {
        return value
    }
}
