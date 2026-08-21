package br.com.renan.vinylcollection.data.network.dto

import com.google.gson.annotations.SerializedName

data class DiscogsSearchResponse(
    val results: List<SearchResultItem>
)

data class SearchResultItem(
    val id: Int,
    val title: String,
    @SerializedName("cover_image")
    val coverImage: String?,
    val barcode: List<String>?,
    val year: String?,
    val genre: List<String>?
)