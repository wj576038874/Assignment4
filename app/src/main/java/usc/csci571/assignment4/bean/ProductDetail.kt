package usc.csci571.assignment4.bean


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
    val ReturnPolicy: ReturnPolicy? = null,
    val Seller: Seller? = null,
    val GlobalShipping: Boolean = false,
    val HandlingTime: Int = 0,
    val ConditionDescription: String? = null,
    val Storefront: Storefront? = null,
)

data class Storefront(
    val StoreURL: String? = null,
    val StoreName: String? = null,
)

data class Seller(
    val UserID: String? = null,
    val FeedbackRatingStar: String? = null,
    val FeedbackScore: String? = null,
    val PositiveFeedbackPercent: Float = 0f
)

data class ReturnPolicy(
    val Refund: String? = null,
    val ReturnsWithin: String? = null,
    val ReturnsAccepted: String? = null,
    val ShippingCostPaidBy: String? = null,
    val InternationalReturnsAccepted: String? = null
)

data class Price(
    val Value: String? = null,
    val CurrencyID: String? = null
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