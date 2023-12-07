package usc.csci571.assignment4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import usc.csci571.assignment4.adapter.ProductDetailPageAdapter
import usc.csci571.assignment4.bean.ProductDetail
import usc.csci571.assignment4.bean.ProductsInfo
import usc.csci571.assignment4.databinding.ActivityProductDetailBinding
import usc.csci571.assignment4.fragment.PhotoFragment
import usc.csci571.assignment4.fragment.ProductFragment
import usc.csci571.assignment4.fragment.ShippingFragment
import usc.csci571.assignment4.fragment.SimilarFragment
import usc.csci571.assignment4.http.ApiService
import usc.csci571.assignment4.http.RetrofitHelper
import usc.csci571.assignment4.viewmodel.CartOperationEvent
import usc.csci571.assignment4.viewmodel.InteractionViewModel
import usc.csci571.assignment4.viewmodel.LiveDataEventBus

/**
 * author: wenjie
 * date: 2023/12/6 09:49
 * description:
 */
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    private var productsInfo: ProductsInfo? = null

    private val tabs = listOf(
        "PRODUCT" to R.drawable.information_variant_selected,
        "SHIPPING" to R.drawable.truck_delivery_selected,
        "PHOTOS" to R.drawable.google_selected,
        "SIMILAR" to R.drawable.equal_selected
    )

    private val viewModel by viewModels<InteractionViewModel>()

    private lateinit var pagerAdapter: ProductDetailPageAdapter

    private val apiService by lazy(LazyThreadSafetyMode.NONE) {
        RetrofitHelper.getRetrofit().create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val param = intent?.getStringExtra("product")
        productsInfo = Gson().fromJson(param, ProductsInfo::class.java)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = productsInfo?.title.toString()
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        pagerAdapter = ProductDetailPageAdapter(
            listOf(
                //运费传递进去
                ProductFragment.newInstance(
                    productsInfo?.shippingInfo?.get(0)?.shippingServiceCost?.get(
                        0
                    )?.value
                ),
                ShippingFragment.newInstance(
                    productsInfo?.shippingInfo?.get(0)?.shippingServiceCost?.get(
                        0
                    )?.value
                ),
                PhotoFragment.newInstance(),
                SimilarFragment.newInstance(),
            ),
            this
        )
        binding.viewPager2.adapter = pagerAdapter

        if (productsInfo?.isCollected == true) {
            binding.btnCart.setImageResource(R.drawable.ic_cart_remove)
        } else {
            binding.btnCart.setImageResource(R.drawable.ic_cart_plus)
        }

        binding.btnCart.setOnClickListener {
            if (param.isNullOrBlank()) return@setOnClickListener
            if (productsInfo?.isCollected == true) {
                //删除
                lifecycleScope.launch {
                    try {
                        apiService.del(item = param)
                        //success
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "${
                                productsInfo?.title?.get(0)?.substring(0, 10)
                            }... was removed from wishlist",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnCart.setImageResource(R.drawable.ic_cart_plus)
                        productsInfo?.isCollected = false
                        LiveDataEventBus.instance.postCartOperation(CartOperationEvent(false , productsInfo?.itemId?.get(0)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "Fetch Error Please Try Again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {

                    }
                }
            } else {
                //添加
                lifecycleScope.launch {
                    try {
                        apiService.add(item = param)
                        //success
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "${
                                productsInfo?.title?.get(0)?.substring(0, 10)
                            }... was add to wishlist",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnCart.setImageResource(R.drawable.ic_cart_remove)
                        productsInfo?.isCollected = true
                        LiveDataEventBus.instance.postCartOperation(CartOperationEvent(true , productsInfo?.itemId?.get(0)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "Fetch Error Please Try Again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {

                    }
                }
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = tabs[position].first
            tab.setIcon(tabs[position].second)
        }.attach()

        lifecycleScope.launch {
            try {
                val itemId = productsInfo?.itemId?.get(0)
                val itemTitle = productsInfo?.title?.get(0)
                if (itemId.isNullOrBlank() || itemTitle.isNullOrBlank()) {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Wrong Parameter",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
//                val productResponse = apiService.productDetailQuery(itemId, itemTitle)
                val productResponse = withContext(Dispatchers.IO) {
                    Gson().fromJson(
                        """{"itemDetails":{"BestOfferEnabled":false,"Description":"<div><div><div>ISSUE(S)</div><div><br></div><div>&nbsp;- No Face ID - Face ID (used as a password lock) does not function</div></div><div><br></div><div>Product description</div></div><div><br></div><div>- Good condition</div><div>- Unlocked device</div><div>- Fully functional tested, all original parts</div><div><br></div><div>Pictures</div><div><br></div><div>- No additional images are provided. You will get the device as described in the listing</div><div>- Please read the damage section to see what the cosmetic issues are with the device</div><div>Prices</div><div>- Prices are fixed</div><div><br></div><div>Shipping</div><div><br></div><div>- Phone ships same business day if bought by 2p PT; next business day if bought after 3p PT</div><div>- Priority FedEx shipping included – 2-3 day guaranteed nationwide</div><div><br></div><div>Returns</div><div><br></div><div>- You can request a return for any reason up to 30 days after shipment</div><div>- Devices will have to be reset to factory condition - no user locks, no iCloud for a full refund</div><div>- Any physical / water damage on the device will void all warranties</div>","ItemID":"155302828200","EndTime":"2023-12-10T20:52:48.000Z","StartTime":"2022-12-10T20:52:48.000Z","ViewItemURLForNaturalSearch":"https://www.ebay.com/itm/Apple-iPhone-12-64GB-Good-Condition-All-Colors-No-Face-ID-/155302828200?var=","ListingType":"FixedPriceItem","Location":"El Segundo, California","PaymentMethods":[],"PictureURL":["https://i.ebayimg.com/00/s/MTYwMFgxMjAw/z/2YkAAOSwQjNjfglA/_57.PNG?set_id=8800005007","https://i.ebayimg.com/00/s/MTYwMFgxMjAw/z/eeQAAOSwmMhjfglE/_57.PNG?set_id=8800005007","https://i.ebayimg.com/00/s/MTYwMFgxMjAw/z/jH8AAOSwsqxjfglI/_57.PNG?set_id=8800005007","https://i.ebayimg.com/00/s/MTYwMFgxMjAw/z/-XMAAOSwasBjlLj1/_57.JPG?set_id=8800005007","https://i.ebayimg.com/00/s/MTYwMFgxMjAw/z/tcYAAOSwDT5jlLj2/_57.JPG?set_id=8800005007","https://i.ebayimg.com/00/s/MTYwMFgxMjAw/z/Fy8AAOSw5aRjpMko/_57.PNG?set_id=8800005007","https://i.ebayimg.com/00/s/MTIwMFgxNjAw/z/jy0AAOSwo75jfglP/_57.PNG?set_id=8800005007","https://i.ebayimg.com/00/s/MTYwMFgxMjAw/z/MGYAAOSwftRjfglU/_57.PNG?set_id=8800005007","https://i.ebayimg.com/00/s/MTYwMFgxMjAw/z/LwgAAOSw5OdjfglS/_57.PNG?set_id=8800005007"],"PostalCode":"902**","PrimaryCategoryID":"9355","PrimaryCategoryName":"Cell Phones & Accessories:Cell Phones & Smartphones","Quantity":149,"Seller":{"UserID":"myphoneclub","FeedbackRatingStar":"Turquoise","FeedbackScore":250,"PositiveFeedbackPercent":92},"BidCount":0,"ConvertedCurrentPrice":{"Value":220,"CurrencyID":"USD"},"CurrentPrice":{"Value":220,"CurrencyID":"USD"},"ListingStatus":"Active","QuantitySold":137,"ShipToLocations":["US"],"Site":"US","TimeLeft":"P4DT6H30M37S","Title":"Apple iPhone 12 64GB - Good Condition - All Colors- No Face ID","ItemSpecifics":{"NameValueList":[{"Name":"Processor","Value":["Hexa Core"]},{"Name":"Screen Size","Value":["6.1 in"]},{"Name":"Manufacturer Color","Value":["Black, Blue, Green, White, Purple , RED"]},{"Name":"Bundled Items","Value":["Adapter, Cable"]},{"Name":"Chipset Model","Value":["Apple A14 Bionic"]},{"Name":"Custom Bundle","Value":["No"]},{"Name":"Battery Health/Capacity","Value":["80% or higher"]},{"Name":"Memory Card Type","Value":["CompactFlash"]},{"Name":"SIM Card Slot","Value":["Single SIM"]},{"Name":"Brand","Value":["Apple"]},{"Name":"Model","Value":["Apple iPhone 12"]},{"Name":"Connectivity","Value":["4G","Bluetooth","GPS","Lightning","LTE","NFC","Wi-Fi"]},{"Name":"Style","Value":["Slate"]},{"Name":"Operating System","Value":["iOS"]},{"Name":"Features","Value":["Bluetooth Enabled","Dual Rear Cameras","Email, Web","Facial Recognition","Fast Charging","Fast Wireless Charging","Front Camera","GPS","Tap to Wake","Ultra Wide-Angle Camera"]},{"Name":"Country/Region of Manufacture","Value":["United States"]},{"Name":"Contract","Value":["Without Contract"]},{"Name":"Camera Resolution","Value":["12.0 MP"]},{"Name":"RAM","Value":["4 GB"]},{"Name":"Release Date","Value":["2020"]}]},"PrimaryCategoryIDPath":"15032:9355","Storefront":{"StoreURL":"https://www.ebay.com/str/2ndphoneclub","StoreName":"Myphoneclub"},"Country":"US","ReturnPolicy":{"Refund":"Money back or replacement (buyer's choice)","ReturnsWithin":"30 Days","ReturnsAccepted":"Returns Accepted","ShippingCostPaidBy":"Seller","InternationalReturnsAccepted":"ReturnsNotAccepted"},"AutoPay":true,"PaymentAllowedSite":[],"IntegratedMerchantCreditCardEnabled":false,"HandlingTime":1,"ConditionID":3000,"ConditionDisplayName":"Used","QuantityAvailableHint":"MoreThan","QuantityThreshold":10,"ExcludeShipToLocation":["Alaska/Hawaii","US Protectorates","APO/FPO"],"GlobalShipping":false,"ConditionDescription":"Grade B phone is in good condition and fully functional.- Phone is in good condition and fully functional.- Main lens will have multiple scratches that can be felt with a fingernail.- Scratches can be on multiple areas of the main lens.- Frame can also have bumps or chips.- Back of the device will have scratches.- Body can also have discoloration or delamination.","QuantitySoldByPickupInStore":0,"NewBestOffer":false},"similarItems":[{"itemId":"295071800121","title":"Apple iPhone 12 64GB No Face ID Factory Unlocked Good Condition","viewItemURL":"https://www.ebay.com/itm/295071800121?_trkparms=amclksrc%3DITM%26mehot%3Dpp%26itm%3D295071800121%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P24DT9H47M35S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/kKQAAOSweqBlAH2y/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"259.95"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"196113910321","title":"Apple iPhone 12 Mini 64GB (Blue) - Unlocked - No Face ID","viewItemURL":"https://www.ebay.com/itm/196113910321?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D196113910321%26pmt%3D0%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-GB","timeLeft":"P4DT3H54M7S","primaryCategoryId":"15032","primaryCategoryName":"Mobile Phones & Communication","country":"GB","imageURL":"https://i.ebayimg.com/thumbs/images/g/xMwAAOSwhFVlbLNQ/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"0.00"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"},"currentPrice":{"@currencyId":"USD","__value__":"36.56"},"bidCount":"7"},{"itemId":"285590699100","title":"Apple iPhone 12 mini - 64GB - Green (Unlocked)","viewItemURL":"https://www.ebay.com/itm/285590699100?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D285590699100%26pmt%3D0%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P2DT13H47M55S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/s1gAAOSwHz5lYXIn/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"250.00"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"},"currentPrice":{"@currencyId":"USD","__value__":"162.50"},"bidCount":"3"},{"itemId":"404655670265","title":"Apple iPhone 12 - 128GB - Blue (Unlocked) (CA)","viewItemURL":"https://www.ebay.com/itm/404655670265?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D404655670265%26pmt%3D0%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P26DT1H8M49S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/ANoAAOSw0Q9lafwn/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"275.00"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"294821173023","title":"Apple iPhone 12 64GB No Face ID Factory Unlocked Very Good Condition","viewItemURL":"https://www.ebay.com/itm/294821173023?_trkparms=amclksrc%3DITM%26mehot%3Dpp%26itm%3D294821173023%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P12DT7H46M24S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/DtcAAOSwwFJlAH0Z/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"264.95"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"276204372159","title":"Apple iPhone 12 - 128GB - Black (Unlocked)","viewItemURL":"https://www.ebay.com/itm/276204372159?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D276204372159%26pmt%3D0%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P26DT7H2M15S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/u7oAAOSw6XBlXSu9/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"240.00"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"115999159155","title":"Apple iPhone XS Max 64GB 256GB - Unlocked, NO FACE ID, All Colours Very Good","viewItemURL":"https://www.ebay.com/itm/115999159155?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D115999159155%26pmt%3D0%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-GB","timeLeft":"P5DT3H7M49S","primaryCategoryId":"15032","primaryCategoryName":"Mobile Phones & Communication","country":"GB","imageURL":"https://i.ebayimg.com/thumbs/images/g/xNMAAOSwNrFlbIFY/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"0.00"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"},"currentPrice":{"@currencyId":"USD","__value__":"63.06"},"bidCount":"0"},{"itemId":"375101757690","title":"Apple iPhone 8 Plus A1864 UNLOCKED Space Gray 64GB iOS 16.7.2","viewItemURL":"https://www.ebay.com/itm/375101757690?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D375101757690%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P30DT0H24M25S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/nLQAAOSwTFplbzex/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"120.00"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"225881982947","title":"Apple iPhone 11 64GB - Sprint locked - Used Good -  (NO FACE ID) - All colors","viewItemURL":"https://www.ebay.com/itm/225881982947?_trkparms=amclksrc%3DITM%26mehot%3Dlo%26itm%3D225881982947%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P15DT8H12M7S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/1wAAAOSwCm9kC0rB/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"164.99"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"266551458962","title":"IPhone 12 Pro 256GB UNLOCKED Great Condition Color Blue Scratches On Frame","viewItemURL":"https://www.ebay.com/itm/266551458962?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D266551458962%26pmt%3D0%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P2DT9H42M38S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/otAAAOSwrmZlb7pb/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"0.00"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"},"currentPrice":{"@currencyId":"USD","__value__":"250.00"},"bidCount":"0"},{"itemId":"256272167010","title":"Apple iPhone 12 - 64GB - Red - Unlocked Smartphone - Good Condition","viewItemURL":"https://www.ebay.com/itm/256272167010?_trkparms=amclksrc%3DITM%26mehot%3Dpp%26itm%3D256272167010%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P21DT8H34M12S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/dYwAAOSwpOhlZkfR/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"279.95"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"294301057065","title":"Apple iPhone 12 64GB Factory Unlocked AT&T T-Mobile Verizon Good Condition","viewItemURL":"https://www.ebay.com/itm/294301057065?_trkparms=amclksrc%3DITM%26mehot%3Dpp%26itm%3D294301057065%26pmt%3D0%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P22DT3H15M24S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/Mb4AAOSw7oRlAHju/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"284.95"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"395041818249","title":"Apple iPhone 12 - 128GB - White (Unlocked) ***READ***","viewItemURL":"https://www.ebay.com/itm/395041818249?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D395041818249%26pmt%3D0%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P29DT1H38M27S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/eu0AAOSw4mBlbfdT/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"189.00"},"shippingCost":{"@currencyId":"USD","__value__":"4.80"}},{"itemId":"256260221310","title":"Apple iPhone 12 Blue 64GB Factory Unlocked - GOOD Condition","viewItemURL":"https://www.ebay.com/itm/256260221310?_trkparms=amclksrc%3DITM%26mehot%3Dpp%26itm%3D256260221310%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P11DT9H12M54S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/U3MAAOSwubtlZkzv/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"279.95"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"314997667886","title":"Apple iPhone 12 Pro Max - 256GB - Gold (Verizon)","viewItemURL":"https://www.ebay.com/itm/314997667886?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D314997667886%26pmt%3D0%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P0DT3H2M42S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/KMYAAOSwIMZlZ3NL/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"440.00"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"},"currentPrice":{"@currencyId":"USD","__value__":"202.50"},"bidCount":"16"},{"itemId":"256290623635","title":"Apple iPhone 12 mini - 64GB - Blue (Unlocked) - Very Good Condition","viewItemURL":"https://www.ebay.com/itm/256290623635?_trkparms=amclksrc%3DITM%26mehot%3Dpp%26itm%3D256290623635%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P4DT10H54M3S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/VUcAAOSwDcNlZk0l/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"259.95"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"294293490163","title":"Apple iPhone X 64GB No Face ID Factory Unlocked Very Good Condition","viewItemURL":"https://www.ebay.com/itm/294293490163?_trkparms=amclksrc%3DITM%26mehot%3Dpp%26itm%3D294293490163%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P17DT7H40M5S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/N8oAAOSwDB9lAHh8/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"164.95"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"276151738720","title":"Apple iPhone 12 64GB 128GB Unlocked Smartphone - Very Good","viewItemURL":"https://www.ebay.com/itm/276151738720?_trkparms=amclksrc%3DITM%26mehot%3Dpp%26itm%3D276151738720%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P0DT4H8M9S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/C8cAAOSw33JlSTCt/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"231.00"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"355258663636","title":"Nokia C01 Plus Go Edition 5.45in Android Smartphone Blue /32GB/GSM/CDMA Unlocked","viewItemURL":"https://www.ebay.com/itm/355258663636?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D355258663636%26pmt%3D1%26noa%3D1%26brand%3DBrand&_trksid=p0","globalId":"EBAY-US","timeLeft":"P29DT1H24M22S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/Hv0AAOSwDZ1lUlZu/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"33.29"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}},{"itemId":"404642142963","title":"Apple iPhone 12 - 64GB - White - Unlocked - A2402 - Good Condition - No Face ID","viewItemURL":"https://www.ebay.com/itm/404642142963?_trkparms=amclksrc%3DITM%26mehot%3Dnone%26itm%3D404642142963%26pmt%3D0%26noa%3D1%26brand%3DApple&_trksid=p0","globalId":"EBAY-US","timeLeft":"P18DT6H31M23S","primaryCategoryId":"15032","primaryCategoryName":"Cell Phones & Accessories","country":"US","imageURL":"https://i.ebayimg.com/thumbs/images/g/wvsAAOSw2EJk-pvA/s-l140.jpg","shippingType":"NotSpecified","buyItNowPrice":{"@currencyId":"USD","__value__":"269.99"},"shippingCost":{"@currencyId":"USD","__value__":"0.00"}}],"photosResponse":[{"kind":"customsearch#result","title":"Apple iPhone 12 - 64GB - White (Unlocked) A2172 Smartphone *No ...","htmlTitle":"<b>Apple iPhone 12</b> - <b>64GB</b> - White (Unlocked) A2172 Smartphone *<b>No</b> ...","link":"https://i.ebayimg.com/images/g/DIUAAOSw2UBlAYyH/s-l1600.jpg","displayLink":"www.ebay.com","snippet":"Apple iPhone 12 - 64GB - White (Unlocked) A2172 Smartphone *No ...","htmlSnippet":"<b>Apple iPhone 12</b> - <b>64GB</b> - White (Unlocked) A2172 Smartphone *<b>No</b> ...","mime":"image/jpeg","fileFormat":"image/jpeg","image":{"contextLink":"https://www.ebay.com/itm/295952872088?chn=ps&mkevt=1&mkcid=28","height":1200,"width":1600,"byteSize":582477,"thumbnailLink":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOqy3Xo-_Eox4AgNwSwylxkh66Zlj6eB_DjWIwklhMtq2hU6QbF9LZeQ&s","thumbnailHeight":113,"thumbnailWidth":150}},{"kind":"customsearch#result","title":"Pre-Owned Apple iPhone 12 Mini 64GB Blue Fully Unlocked (No Face ...","htmlTitle":"Pre-Owned <b>Apple iPhone 12</b> Mini <b>64GB</b> Blue Fully Unlocked (<b>No Face</b> ...","link":"https://i5.walmartimages.com/seo/Pre-Owned-Apple-iPhone-12-Mini-64GB-Blue-Fully-Unlocked-No-Face-ID-Refurbished-Good_d7b20c73-c609-49a4-aefb-02e3c65eeb6d.0259edfd071afd2906c58cbe55c84d85.jpeg","displayLink":"www.walmart.com","snippet":"Pre-Owned Apple iPhone 12 Mini 64GB Blue Fully Unlocked (No Face ...","htmlSnippet":"Pre-Owned <b>Apple iPhone 12</b> Mini <b>64GB</b> Blue Fully Unlocked (<b>No Face</b> ...","mime":"image/jpeg","fileFormat":"image/jpeg","image":{"contextLink":"https://www.walmart.com/ip/Pre-Owned-Apple-iPhone-12-Mini-64GB-Blue-Fully-Unlocked-No-Face-ID-Refurbished-Good/873472093","height":2224,"width":1880,"byteSize":250618,"thumbnailLink":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPWUTXgUPfhxgzzeh8ZqP_7FFt1ghX0Xc5xsdBrF-V2dezdW1WWW7o9Q&s","thumbnailHeight":150,"thumbnailWidth":127}},{"kind":"customsearch#result","title":"Apple iPhone 12 5G 64GB Purple (AT&T) MJNE3LL/A - Best Buy","htmlTitle":"Apple iPhone 12 5G 64GB Purple (AT&T) MJNE3LL/A - Best Buy","link":"https://pisces.bbystatic.com/image2/BestBuy_US/images/products/6443/6443391_sd.jpg","displayLink":"www.bestbuy.com","snippet":"6443391_sd.jpg","htmlSnippet":"6443391_sd.jpg","mime":"image/jpeg","fileFormat":"image/jpeg","image":{"contextLink":"https://www.bestbuy.com/site/apple-iphone-12-5g-64gb-purple-at-t/6443391.p?skuId=6443391","height":3088,"width":2270,"byteSize":304029,"thumbnailLink":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSI7G62x_gThLF_t433-PGAmx_xTLQkEAeutZImw0VUJERGVrSdBtm_HA&s","thumbnailHeight":150,"thumbnailWidth":110}},{"kind":"customsearch#result","title":"Pre-Owned Apple iPhone 12 Mini 64GB Green Fully Unlocked (No Face ...","htmlTitle":"Pre-Owned <b>Apple iPhone 12</b> Mini <b>64GB</b> Green Fully Unlocked (<b>No Face</b> ...","link":"https://i5.walmartimages.com/seo/Pre-Owned-Apple-iPhone-12-Mini-64GB-Green-Fully-Unlocked-No-Face-ID-Refurbished-Good_d1e8b373-753f-4364-be6b-6bb8d0c8e17b.353590c1f64b933132cba102ec25af61.jpeg","displayLink":"www.walmart.com","snippet":"Pre-Owned Apple iPhone 12 Mini 64GB Green Fully Unlocked (No Face ...","htmlSnippet":"Pre-Owned <b>Apple iPhone 12</b> Mini <b>64GB</b> Green Fully Unlocked (<b>No Face</b> ...","mime":"image/jpeg","fileFormat":"image/jpeg","image":{"contextLink":"https://www.walmart.com/ip/Pre-Owned-Apple-iPhone-12-Mini-64GB-Green-Fully-Unlocked-No-Face-ID-Refurbished-Good/997398697","height":2224,"width":1880,"byteSize":225863,"thumbnailLink":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTp3giFCJ1vjF0ZYHpmnEDGKEBk3O8BQGVG1tQG1fZrGtrzrIA8VphMiM8&s","thumbnailHeight":150,"thumbnailWidth":127}},{"kind":"customsearch#result","title":"Apple iPhone X 64GB - NO FACE ID - Good Condition (Refurbished)","htmlTitle":"<b>Apple iPhone</b> X <b>64GB</b> - <b>NO FACE ID</b> - <b>Good Condition</b> (Refurbished)","link":"https://www.perfectpreowned.com.au/assets/full/K-iPH-X-64GB-GNID-2.jpg?20210628174641","displayLink":"www.perfectpreowned.com.au","snippet":"Apple iPhone X 64GB - NO FACE ID - Good Condition (Refurbished)","htmlSnippet":"<b>Apple iPhone</b> X <b>64GB</b> - <b>NO FACE ID</b> - <b>Good Condition</b> (Refurbished)","mime":"image/jpeg","fileFormat":"image/jpeg","image":{"contextLink":"https://www.perfectpreowned.com.au/apple-iphone-x-64gb-no-face-id-good-condition-refu","height":1505,"width":1599,"byteSize":165891,"thumbnailLink":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTDMPr_VmGSNdXz8fc7ULOuMYLUieSxqVLvuaj8fPt9mYCGCEQvSmQTzgk&s","thumbnailHeight":141,"thumbnailWidth":150}},{"kind":"customsearch#result","title":"Pre-Owned Apple iPhone 12 Mini 64GB Blue Fully Unlocked (No Face ...","htmlTitle":"Pre-Owned <b>Apple iPhone 12</b> Mini <b>64GB</b> Blue Fully Unlocked (<b>No Face</b> ...","link":"https://i5.walmartimages.com/seo/Apple-iPhone-12-Mini-64GB-Fully-Unlocked-Purple-NO-FACE-ID-Refurbished-Good_d29fecbd-75c6-49ee-8940-f0686d4e69b4.1c1eebe3bc85917b80c209f41c3a3ec9.jpeg","displayLink":"www.walmart.com","snippet":"Pre-Owned Apple iPhone 12 Mini 64GB Blue Fully Unlocked (No Face ...","htmlSnippet":"Pre-Owned <b>Apple iPhone 12</b> Mini <b>64GB</b> Blue Fully Unlocked (<b>No Face</b> ...","mime":"image/jpeg","fileFormat":"image/jpeg","image":{"contextLink":"https://www.walmart.com/ip/Pre-Owned-Apple-iPhone-12-Mini-64GB-Blue-Fully-Unlocked-No-Face-ID-Refurbished-Good/873472093","height":2560,"width":2560,"byteSize":167388,"thumbnailLink":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8_75HdadLBaxDDK45fU-8R_UEP0FCJrr24-9S2s09bRhTyDUX-N-jXwWO&s","thumbnailHeight":150,"thumbnailWidth":150}},{"kind":"customsearch#result","title":"Buy 10.9-inch iPad Wi‑Fi 64GB - Pink - Apple","htmlTitle":"Buy 10.9-inch iPad Wi‑Fi <b>64GB</b> - Pink - <b>Apple</b>","link":"https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/ipad-10th-gen-finish-select-202212-pink-wifi?wid=5120&hei=2880&fmt=p-jpg&qlt=95&.v=1670856076309","displayLink":"www.apple.com","snippet":"Buy 10.9-inch iPad Wi‑Fi 64GB - Pink - Apple","htmlSnippet":"Buy 10.9-inch iPad Wi‑Fi <b>64GB</b> - Pink - <b>Apple</b>","mime":"image/","fileFormat":"image/","image":{"contextLink":"https://www.apple.com/shop/buy-ipad/ipad/64gb-pink-wifi","height":2880,"width":5120,"byteSize":527794,"thumbnailLink":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcThHwF1hH_spbDCRiKiBru52rFeGbIMmUEuvrFgnj_e6rFfuex9pK0Rwg&s","thumbnailHeight":84,"thumbnailWidth":150}},{"kind":"customsearch#result","title":"Apple iPhone 12 Mini 64GB 128GB 256GB All Colors - Factory ...","htmlTitle":"<b>Apple iPhone 12</b> Mini <b>64GB</b> 128GB 256GB <b>All Colors</b> - Factory ...","link":"https://i5.walmartimages.com/asr/72803c78-7209-4996-9644-9cff3048e292.aac9c9ca839fe1f6ae37d76e3b799661.jpeg","displayLink":"www.walmart.com","snippet":"Apple iPhone 12 Mini 64GB 128GB 256GB All Colors - Factory ...","htmlSnippet":"<b>Apple iPhone 12</b> Mini <b>64GB</b> 128GB 256GB <b>All Colors</b> - Factory ...","mime":"image/jpeg","fileFormat":"image/jpeg","image":{"contextLink":"https://www.walmart.com/ip/Apple-iPhone-12-Mini-64GB-128GB-256GB-All-Colors-Factory-Unlocked-Cell-Phone-Good-Condition/977000427","height":2000,"width":2000,"byteSize":154959,"thumbnailLink":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRwVhQGycf2MQxIVivYBWEcxbGsdiJ_G-wj6E81AXx23U4vkLcvjEsU3SLE&s","thumbnailHeight":150,"thumbnailWidth":150}}]}""",
                        ProductDetail::class.java
                    )
                }
                viewModel.postDetail(productResponse)
                binding.viewPager2.visible()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ProductDetailActivity, "Fetching Error", Toast.LENGTH_SHORT)
                    .show()
            } finally {
                binding.progress.gone()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 100, 0, "share")?.setIcon(R.drawable.icons_facebook)
            ?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 100) {
            Toast.makeText(this, "Share Facebook", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {

        fun Context.startProductDetail(item: String) {
            startActivity(Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("product", item)
            })
        }

        fun Fragment.startProductDetail(item: String) {
            startActivity(Intent(requireContext(), ProductDetailActivity::class.java).apply {
                putExtra("product", item)
            })
        }
    }
}