package usc.csci571.assignment4.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * author: wenjie
 * date: 2023/6/2 15:28
 * description:
 */
class RetrofitHelper {

    companion object {
        @Volatile
        private var retrofit: Retrofit? = null

        @JvmStatic
        fun getRetrofit(): Retrofit = retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder().baseUrl("https://csci571-xm-assignment3.wl.r.appspot.com")
                .client(OkHttpHelper.instance.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create()).build().also {
                    retrofit = it
                }
        }
    }
}