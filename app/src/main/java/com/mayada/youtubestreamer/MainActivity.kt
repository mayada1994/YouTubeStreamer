package com.mayada.youtubestreamer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.mayada.youtubestreamer.repositories.Repository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NullPointerException
import java.util.regex.Pattern
import android.media.MediaPlayer
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    lateinit var streamUrl: String
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<TextView>(R.id.search_btn)
        btn.setOnClickListener {
            Song.artist = findViewById<EditText>(R.id.edit_song_artist).text.toString().replace(" ", "+")
            Song.album = findViewById<EditText>(R.id.edit_song_album).text.toString().replace(" ", "+")
            Song.title = findViewById<EditText>(R.id.edit_song_title).text.toString().replace(" ", "+")
            val url: String = getString(R.string.base_url) + Song.artist + "/" + Song.album + "/" + Song.title + "/"
            findViewById<TextView>(R.id.song_url).text = url
            val repository = Repository(url)
            repository.getData().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        streamUrl = response.body()!!.string()
                        findViewById<TextView>(R.id.song_url).text = streamUrl

                        val pattern = Pattern.compile(
                            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                            Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
                        )
                        val matcher = pattern.matcher(streamUrl)
                        var matchStart = 0
                        var matchEnd = 1
                        while (matcher.find()) {
                            matchStart = matcher.start(1)
                            matchEnd = matcher.end()
                            // now you have the offsets of a URL match

                            val substr = streamUrl.substring(matchStart, matchEnd)
                            if (substr.contains("https://www.youtube.com/watch?")) {
                                findViewById<TextView>(R.id.song_url).text = substr
                                Log.d("TAG!!!", substr)
                                fetchStream(substr)
                                break
                            }
                        }

                    } catch (e: NullPointerException) {
                        Log.e("GitHubResponse", response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("GitHubResponse", "Profile not found")
                }
            })
        }
    }

    fun fetchStream(substrStream: String) {
        if (substrStream != "") {
            val streamRepo =
                Repository("http://michaelbelgium.me/ytconverter/")
            streamRepo.getStream(substrStream).enqueue(object : Callback<Stream> {
                override fun onResponse(
                    call: Call<Stream>,
                    response: Response<Stream>
                ) {
                    try {
                        if (response.body()!!.error != "true") {
                            streamUrl = response.body()!!.file
                            Log.d("TAG!!!", streamUrl)
                            findViewById<TextView>(R.id.song_url).text = streamUrl
                            playAudio(streamUrl)
                        }else{
                            Toast.makeText(this@MainActivity, "Song not found", Toast.LENGTH_LONG).show()
                        }

                    } catch (e: NullPointerException) {
                        Log.e("GitHubResponse", response.message())
                    }
                }

                override fun onFailure(call: Call<Stream>, t: Throwable) {
                    Log.d("GitHubResponse", "Profile not found")
                }
            })
        }
    }

    @Throws(Exception::class)
    private fun playAudio(url: String) {
        killMediaPlayer()

        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource(url)
        mediaPlayer!!.prepare()
        mediaPlayer!!.start()
    }

    private fun killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer!!.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
