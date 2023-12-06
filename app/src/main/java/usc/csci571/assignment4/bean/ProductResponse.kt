package usc.csci571.assignment4.bean

import com.google.gson.annotations.SerializedName

/**
 * author: wenjie
 * date: 2023/12/5 18:14
 * description:
 */
data class ProductResponse(
    @SerializedName(value = "productsInfo", alternate = ["favoriteList"])
    val productsInfo: List<ProductsInfo>? = null
)

data class ProductsInfo(
    @SerializedName("id")
    val id: String? = null,
    //本地存储方便做搜藏可取消收藏
    var isCollected: Boolean = false,
    val itemId: List<String>? = null,
    val title: List<String>? = null,
    val globalId: List<String>? = null,
    val primaryCategory: List<Category>? = null,
    val galleryURL: List<String>? = null,
    val viewItemURL: List<String>? = null,
    val autoPay: List<String>? = null,
    val postalCode: List<String>? = null,
    val location: List<String>? = null,
    val country: List<String>? = null,
    val storeInfo: List<StoreInfo>? = null,
    val sellerInfo: List<SellerInfo>? = null,
    val shippingInfo: List<ShippingInfo>? = null,
    val sellingStatus: List<SellingStatus>? = null,
    val listingInfo: List<ListingInfo>? = null,
    val returnsAccepted: List<String>? = null,
    val distance: List<Distance>? = null,
    val condition: List<Condition>? = null,
    val isMultiVariationListing: List<String>? = null,
    val discountPriceInfo: List<DiscountPriceInfo>? = null,
    val topRatedListing: List<String>? = null,
)

data class DiscountPriceInfo(
    val originalRetailPrice: List<KeyValue>? = null,
    val pricingTreatment: List<String>? = null,
    val soldOnEbay: List<String>? = null,
    val soldOffEbay: List<String>? = null,
)

data class Distance(
    @SerializedName("@unit")
    val unit: String? = null,
    @SerializedName("__value__")
    val value: String? = null
)

data class ListingInfo(
    val bestOfferEnabled: List<String>? = null,
    val buyItNowAvailable: List<String>? = null,
    val startTime: List<String>? = null,
    val endTime: List<String>? = null,
    val listingType: List<String>? = null,
    val gift: List<String>? = null,
    val watchCount: List<String>? = null,
)

data class Category(
    val categoryId: List<String>? = null,
    val categoryName: List<String>? = null
)

data class StoreInfo(
    val storeName: List<String>? = null,
    val storeURL: List<String>? = null,
)

data class SellerInfo(
    val sellerUserName: List<String>? = null,
    val feedbackScore: List<String>? = null,
    val positiveFeedbackPercent: List<String>? = null,
    val feedbackRatingStar: List<String>? = null,
    val topRatedSeller: List<String>? = null,
)

data class ShippingInfo(
    val shippingServiceCost: List<KeyValue>? = null,
    val shippingType: List<String>? = null,
    val shipToLocations: List<String>? = null,
    val expeditedShipping: List<String>? = null,
    val oneDayShippingAvailable: List<String>? = null,
    val handlingTime: List<String>? = null,
)

data class KeyValue(
    @SerializedName("@currencyId")
    val currencyId: String? = null,
    @SerializedName("__value__")
    val value: String? = null
)

data class SellingStatus(
    val currentPrice: List<CurrentPrice>? = null,
    val convertedCurrentPrice: List<CurrentPrice>? = null,
    val sellingState: List<String>? = null,
    val timeLeft: List<String>? = null,
)

data class Condition(
    val conditionId: List<String>? = null,
    val conditionDisplayName: List<String>? = null
)

data class CurrentPrice(
    @SerializedName(value = "@currencyId", alternate = ["CurrencyID"])
    val currencyId: String? = null,
    @SerializedName(value = "__value__", alternate = ["Value"])
    val value: String? = null
)