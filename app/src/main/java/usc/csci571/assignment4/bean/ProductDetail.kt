package usc.csci571.assignment4.bean

import com.google.gson.annotations.SerializedName

/**
 * author: wenjie
 * date: 2023/12/6 11:12
 * description:
 */
data class ProductDetail(
    val itemDetails: ItemDetails? = null,
    val photosResponse: List<Photo>? = null,
    val similarItems: List<SimilarItem>? = null
)

data class SimilarItem(
    val itemId: String? = null,
    val title: String? = null,
    val viewItemURL: String? = null,
    val globalId: String? = null,
    val timeLeft: String? = null,
    var timeLeftInt: Int = 0,
    val primaryCategoryId: String? = null,
    val primaryCategoryName: String? = null,
    val country: String? = null,
    val imageURL: String? = null,
    val shippingType: String? = null,
    val buyItNowPrice: KeyValue? = null,
    val shippingCost: KeyValue? = null,
)


data class ItemDetails(
    val PictureURL: List<String>? = null,
    val Title: String? = null,
    val CurrentPrice: Price? = null,
    val ItemSpecifics: ItemSpecifics? = null,
)

data class Price(
    @SerializedName("Value")
    val value: String? = null,
    @SerializedName("CurrencyID")
    val currencyID: String? = null
)

data class Photo(
    val link: String? = null
)

data class ItemSpecifics(
    val NameValueList: List<NameValue>? = null,
)

data class NameValue(
    val Name: String? = null,
    val Value: List<String>? = null
)