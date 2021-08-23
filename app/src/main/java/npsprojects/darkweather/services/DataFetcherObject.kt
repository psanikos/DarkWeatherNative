package npsprojects.darkweather.services

import okhttp3.*
import okio.IOException
import java.util.concurrent.TimeUnit

object DataFetcher {

    private val client = OkHttpClient()
    private val cacheControl by lazy {
        CacheControl.Builder().maxStale(8, TimeUnit.HOURS).build()
    }


    fun getFromUrl(url: String, completion: (String?) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .cacheControl(cacheControl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("FAILED GETTING DATA")
                e.printStackTrace()
                completion(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        println("RESPONSE UNSUCCESSFUL")

                        completion(null)
                        throw   IOException("Unexpected code $response")
                    }

                    if (response.body != null) {
                        println("GOT RESPONSE")
                        completion(response.body!!.string())
                    } else {
                        println("RESPONSE NULL")
                        completion(null)
                    }
                }
            }
        })

    }
}