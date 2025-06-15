package com.example.avisonline.data.model

data class KpiConfigResponse(
    @SerializedName("limited_access")
    val limitedAccess: Boolean,
    val message: String
)

data class UserAccount(
    val name: String,
    val surname: String?,
    val email: String,
    val phone: String?
)