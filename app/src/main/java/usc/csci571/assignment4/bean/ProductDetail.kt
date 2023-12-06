package usc.csci571.assignment4.bean

/**
 * author: wenjie
 * date: 2023/12/6 11:12
 * description:
 */
data class ProductDetail(
    val itemDetails: ItemDetails? = null,
)

data class ItemDetails(
    val PictureURL: List<String>? = null,
    val Title: String? = null,
    val CurrentPrice: CurrentPrice? = null,
    val ItemSpecifics: ItemSpecifics? = null,
)

data class ItemSpecifics(
    val NameValueList: List<NameValue>? = null,
)

data class NameValue(
    val Name: String? = null,
    val Value: List<String>? = null
)