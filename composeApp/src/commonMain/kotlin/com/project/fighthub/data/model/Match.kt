package com.project.fighthub.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Match(
    @SerialName("id") val id: String = "",
    @SerialName("user1_id") val user1Id: String,
    @SerialName("user2_id") val user2Id: String,
    @SerialName("status") val status: String = "active",
    @SerialName("winner_id") val winnerId: String? = null,
    @SerialName("created_at") val createdAt: String = ""
)