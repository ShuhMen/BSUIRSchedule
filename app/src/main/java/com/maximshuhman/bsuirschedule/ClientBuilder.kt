package com.maximshuhman.bsuirschedule

import android.app.Activity
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Clientbuilder  {

    private lateinit var retrofit: Retrofit
    var LoginSituated: Boolean = false

    fun getGroupScheduleClient(baseUrl: String): Retrofit? {

        try {

                val client: OkHttpClient = OkHttpClient.Builder()
                    .readTimeout(90, TimeUnit.SECONDS)
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()


        } catch (e: Exception) {

            return null

        }
        return retrofit
    }
}