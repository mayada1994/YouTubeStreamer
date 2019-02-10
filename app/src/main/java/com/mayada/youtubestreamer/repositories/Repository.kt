package com.mayada.youtubestreamer.repositories

import com.mayada.youtubestreamer.Stream
import com.mayada.youtubestreamer.services.YouTubeService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository(url:String) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val gitHubService = retrofit.create(YouTubeService::class.java)

    fun getData(): Call<ResponseBody> {
        return gitHubService.getHtml(this.retrofit.baseUrl().toString())
    }

    fun getStream(link:String): Call<Stream> {
        return gitHubService.getStream(link)
    }

}