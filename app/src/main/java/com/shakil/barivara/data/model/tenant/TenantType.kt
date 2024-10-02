package com.shakil.barivara.data.model.tenant

enum class TenantType(val value: String) {
    BACHELOR("bachelor"),
    SMALL_FAMILY("small_family"),
    FAMILY("family"),
    MALE_ONLY("male_only"),
    FEMALE_ONLY("female_only"),
    OTHERS("others");

    companion object {
        fun from(value: String): TenantType? {
            return entries.find { it.value == value }
        }
    }

    fun toValue(): String {
        return value
    }
}
