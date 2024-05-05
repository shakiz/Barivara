package com.shakil.barivara.model

import com.google.firebase.database.Exclude

class User(
    var id: String = "",
    var firebaseKey: String = "",
    var name: String = "",
    var userName: String = "",
    var mobile: String = "",
    var email: String = "",
    var dOB: String = ""
) {
    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["name"] = name
        result["userName"] = userName
        result["email"] = email
        result["mobile"] = mobile
        result["dOB"] = dOB
        return result
    }
}
