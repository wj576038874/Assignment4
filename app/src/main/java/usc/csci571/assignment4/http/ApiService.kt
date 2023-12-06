package usc.csci571.assignment4.http

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import usc.csci571.assignment4.bean.ProductDetail
import usc.csci571.assignment4.bean.ProductResponse
import usc.csci571.assignment4.bean.ZipCodeResult

/**
 * author: wenjie
 * date: 2023/12/5 17:46
 * description:
 */
interface ApiService {

    //?keyword=iphone&category=58058&currentLocation=30022&distance=12&condition=Used&shipping=FreeShippingOnly
    @GET("/productsQuery")
    suspend fun search(@QueryMap queryMap: Map<String, String>): ProductResponse

    @GET("/modFavorites")
    suspend fun add(@Query("add") add: String = "true", @Query("item") item: String): Any

    @GET("modFavorites")
    suspend fun del(@Query("del") add: String = "true", @Query("item") item: String): Any

    @GET("/queryFavorites")
    suspend fun queryFavorites(): ProductResponse

    @GET("/productDetailQuery")
    suspend fun productDetailQuery(
        @Query("itemId") itemId: String,
        @Query("itemTitle") itemTitle: String
    ): ProductDetail

    @GET("/zipQuery")
    suspend fun getZipCode(@Query("zipcode") zipcode: String): ZipCodeResult
}