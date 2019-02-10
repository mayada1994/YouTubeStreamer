package com.mayada.youtubestreamer

import com.google.gson.annotations.SerializedName

data class Stream(
    @SerializedName("error") val error: String,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("file") val file: String
)