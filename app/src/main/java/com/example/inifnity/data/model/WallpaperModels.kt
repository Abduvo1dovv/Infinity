package com.example.inifnity.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PexelsResponse(
    @Json(name = "photos") val photos: List<PhotoDto>
)


@JsonClass(generateAdapter = true)
data class PhotoDto(
    @Json(name = "id") val id: Int,
    @Json(name = "photographer") val photographer: String,
    @Json(name = "src") val src: SrcDto,
    @Json(name = "alt") val alt: String?
)


@JsonClass(generateAdapter = true)
data class SrcDto(
    @Json(name = "original") val original: String,
    @Json(name = "large2x") val large2x: String,
    @Json(name = "portrait") val portrait: String
)


data class Wallpaper(
    val id: Int,
    val imageUrl: String,
    val fullUrl: String,
    val category: String
)