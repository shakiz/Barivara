package com.shakil.barivara.data.model.tenant

enum class TenantType(val value: String) {
    BACHELOR("bachelor"),
    SMALL_FAMILY("small_family"),
    LARGE_FAMILY("family"),
    MALE_ONLY("male_only"),
    FEMALE_ONLY("female_only"),
    OTHERS("others");

    companion object {
        fun from(value: String): TenantType? {
            return entries.find { it.value == value }
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
