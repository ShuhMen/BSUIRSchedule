package com.maximshuhman.bsuirschedule

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import okhttp3.*
import java.io.IOException


class Requests {
    //var okHttpClient: OkHttpClient = OkHttpClient()
//    var client = OkHttpClient()
    /* suspend fun getGroupSchedule(url: String) {

        val client = HttpClient()

       /* client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "iis.bsuir.by"
                path("/api/v1/schedule?studentGroup=220603")
            }
        }*/

        val response: HttpResponse = client.get("https://iis.bsuir.by/api/v1/schedule?studentGroup=220604")

    Log.d("dsdsd", response.toString())

    }
   /* private fun loadRandomFact() {
        /*  runOnUiThread {
              progressBar.visibility = View.VISIBLE
          }*/

        val request: Request = Request.Builder().url(URL("https://iis.bsuir.by/api/v1/schedule?studentGroup=220603")).build()
        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
                val json = response?.body()?.string()
                //val txt = (JSONObject(json).getJSONObject("value").get("joke")).toString()


            }
        })

    }
*/


    fun run() {

        val request: Request = Request.Builder()
            .url("https://iis.bsuir.by/api/v1/schedule?studentGroup=220603")
            .build()


        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                } else {
                    val json = response.body?.toString()
                   /* MainActivity().runOnUiThread(Runnable {
                        @Override
                        fun run(){
                            M
                        }
                    })*/
                    Data.response = json.toString()
                }
            }
        })
      /*  val request = Request.Builder()
            .url("http://publicobject.com/helloworld.txt")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                         void run() {
                            try {
                                TextView myTextView = (TextView) findViewById(R.id.myTextView);
                                myTextView.setText(responseData);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }
            }
        })*/
    }

*/
}
    class GetExample() {
        private val client = OkHttpClient()
        @Throws(IOException::class)
        fun run(url: String) {
            val request: Request = Request.Builder()
                .url("https://iis.bsuir.by/api/v1/schedule?studentGroup=220603")
                .build()
            client.newCall(request).enqueue(object: Callback {


                override fun onFailure(call: Call, e: IOException) {
                    Log.d("dfdfdfd", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = response?.body?.string()
                    Log.d("fdfdfdfd",response.body.toString() )
                    //val txt = (JSONObject(json).getJSONObject("value").get("joke")).toString()

                }
            })
        }


    }
