package com.shakil.barivara.data.model.auth

enum class OtpUIState(val value: String) {
    SEND_OTP("send_otp"),
    VERIFY_OTP("verify_otp"),
    SETUP_PASS("setup_pass");

    companion object {
        fun from(value: String): OtpUIState? {
            return entries.find { it.value == value }
        }
    }

    fun toValue(): String {
        return value
    }
}
