package br.com.renan.vinylcollection.data.network.dto

import com.google.gson.annotations.SerializedName

data class DiscogsAddReleaseResponse(
    @SerializedName("instance_id")
    val instanceId: Long?
)

data class DiscogsReleaseInstancesResponse(
    val releases: List<DiscogsReleaseInstanceItem>?
)

data class DiscogsReleaseInstanceItem(
    val id: Int,
    @SerializedName("instance_id")
    val instanceId: Long,
    @SerializedName("folder_id")
    val folderId: Int?
)