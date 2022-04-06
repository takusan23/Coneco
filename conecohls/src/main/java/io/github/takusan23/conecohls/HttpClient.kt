package io.github.takusan23.conecohls

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/** OkHttp */
object HttpClient {

    private val okHttpClient = OkHttpClient()

    /**
     * GETリクエストを送信する
     *
     * @param url URL
     * @return レスポンス
     * */
    suspend fun execGetRequest(url: String) = withContext(Dispatchers.IO) {
        val request = Request.Builder().apply {
            url(url)
            get()
        }.build()
        return@withContext okHttpClient.newCall(request).execute()
    }

}