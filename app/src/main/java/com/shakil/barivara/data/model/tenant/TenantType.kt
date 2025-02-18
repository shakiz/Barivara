package com.shakil.barivara.data.model.tenant

import android.content.Context
import com.shakil.barivara.R

enum class TenantType(val value: String) {
    BACHELOR("bachelor"),
    SMALL_FAMILY("small_family"),
    LARGE_FAMILY("family"),
    MALE_ONLY("male_only"),
    FEMALE_ONLY("female_only"),
    OTHERS("others");

    companion object {
        fun fromString(context: Context, value: String): TenantType {
            return when (value) {
                context.getString(R.string.bachelor).lowercase() -> BACHELOR
                context.getString(R.string.small_family).lowercase() -> SMALL_FAMILY
                context.getString(R.string.large_family).lowercase() -> LARGE_FAMILY
                context.getString(R.string.male_only).lowercase() -> MALE_ONLY
                context.getString(R.string.female_only).lowercase() -> FEMALE_ONLY
                else -> OTHERS
            }
        }

        fun toStringType(context: Context, value: TenantType): String {
            return when (value) {
                BACHELOR -> context.getString(R.string.bachelor)
                SMALL_FAMILY -> context.getString(R.string.small_family)
                LARGE_FAMILY -> context.getString(R.string.large_family)
                MALE_ONLY -> context.getString(R.string.male_only)
                FEMALE_ONLY -> context.getString(R.string.female_only)
                OTHERS -> context.getString(R.string.others)
            }
        }

        fun from(value: Int): TenantType {
            return when (value) {
                1 -> BACHELOR
                2 -> SMALL_FAMILY
                3 -> LARGE_FAMILY
                4 -> MALE_ONLY
                5 -> FEMALE_ONLY
                else -> OTHERS
            }
        }
    }

    fun toValue(): String {
        return value
    }

    fun toType(tenantType: TenantType): Int {
        return when (tenantType) {
            BACHELOR -> 1
            SMALL_FAMILY -> 2
            LARGE_FAMILY -> 3
            MALE_ONLY -> 4
            FEMALE_ONLY -> 5
            else -> 6
        }
    }
}
