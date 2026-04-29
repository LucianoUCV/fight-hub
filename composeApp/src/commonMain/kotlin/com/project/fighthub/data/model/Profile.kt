package com.project.fighthub.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String? = null,
    @SerialName("age") val age: Int? = null,
    @SerialName("height") val height: Int? = null,
    @SerialName("weight") val weight: Int? = null,
    @SerialName("elo_points") val eloPoints: Int = 1000,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("lat") val lat: Double? = null,
    @SerialName("lng") val lng: Double? = null
)