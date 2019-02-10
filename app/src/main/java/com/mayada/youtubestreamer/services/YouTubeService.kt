package com.mayada.youtubestreamer.services

import com.mayada.youtubestreamer.Stream
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface YouTubeService {

    @GET
    fun getHtml(@Url url: String): Call<ResponseBody>

    @GET("convert.php?")
    fun getStream(@Query("youtubelink") youtubelink: String): Call<Stream>
}