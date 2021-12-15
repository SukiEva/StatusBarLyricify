package io.github.sukieva.statusBarLyricify.utils

import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import org.json.JSONObject


object EasyOkhttp {

    private val sessionCookieJar = SessionCookieJar()

    private val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .cookieJar(sessionCookieJar)
        .build()

    private fun sendHttpRequest(address: String, referer: String? = null, callback: Callback) {
        val request: Request =
            Request.Builder()
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:77.0) Gecko/20100101 Firefox/77.0")
                .addHeader("Accept", "text/html, application/xhtml+xml, */*")
                .addHeader("Accept-Language", "en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3")
                .addHeader("Accept-Encoding", "deflate")
                .apply {
                    referer?.let { addHeader("Referer", referer) }
                }
                .url(address).build()
        okHttpClient.newCall(request).enqueue(callback)
    }


    suspend fun request(address: String, referer: String? = null): JSONObject? {
        return suspendCoroutine {
            sendHttpRequest(address, referer, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.body == null)
                        it.resume(null)
                    //it.resumeWithException(RuntimeException("response body is null"))
                    println(response.body.toString())
                    it.resume(null)
//                    val resBody = response.body!!.string()
//                    val json = JSONObject(resBody)
//                    it.resume(json)
                }
            })
        }
    }


    // https://stackoverflow.com/questions/38418809/add-cookies-to-retrofit-2-request
    class SessionCookieJar : CookieJar {
        private val cookieStore: HashMap<String, List<Cookie>> = HashMap()
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val cookies = cookieStore[url.host]
            return cookies ?: ArrayList()
        }
    }
}