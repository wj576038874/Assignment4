package usc.csci571.assignment4.http

import okhttp3.OkHttpClient
import usc.csci571.assignment4.LoggerInterceptor
import java.util.concurrent.TimeUnit

/**
 * author: wenjie
 * date: 2023/5/30 17:20
 * description:
 */
class OkHttpHelper private constructor() {

    private val mHttpClient: OkHttpClient


    init {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(LoggerInterceptor("request" , true))
        mHttpClient = builder.build()
    }

    fun getOkHttpClient(): OkHttpClient {
        return mHttpClient
    }

    /**
     * 初始化请求头参数
     */

    companion object {
        private const val TIMEOUT = 30

        @JvmStatic
        val instance by lazy {
            OkHttpHelper()
        }
    }

}