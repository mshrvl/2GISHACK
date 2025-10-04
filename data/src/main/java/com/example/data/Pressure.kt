package com.example.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PressureDto(
    @SerialName("id")
    val id: Int?,
    @SerialName("code")
    val code: String,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("type")
    val type: String
)