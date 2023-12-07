package usc.csci571.assignment4

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import usc.csci571.assignment4.ProductDetailActivity.Companion.startProductDetail
import usc.csci571.assignment4.adapter.ProductListAdapter
import usc.csci571.assignment4.databinding.ActivitySearchResultBinding
import usc.csci571.assignment4.http.ApiService
import usc.csci571.assignment4.http.RetrofitHelper
import usc.csci571.assignment4.viewmodel.CartOperationEvent
import usc.csci571.assignment4.viewmodel.LiveDataEventBus

/**
 * author: wenjie
 * date: 2023/12/5 20:47
 * description:
 */
class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding

    private var keyword: String = ""
    private var category: Int = 0
    private var currentLocation: String? = null
    private var condition: String? = null
    private var distance: String? = null
    private var shipping: String? = null

    private val apiService by lazy(LazyThreadSafetyMode.NONE) {
        RetrofitHelper.getRetrofit().create(ApiService::class.java)
    }

    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ProductListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        keyword = intent?.getStringExtra("keyword") ?: ""
        category = intent?.getIntExtra("category", 0) ?: 0
        currentLocation = intent?.getStringExtra("currentLocation")
        condition = intent?.getStringExtra("condition")
        distance = intent?.getStringExtra("distance") ?: "10"
        shipping = intent?.getStringExtra("shipping")

        Log.e("asd", keyword)
        Log.e("asd", category.toString())
        Log.e("asd", currentLocation.toString())
        Log.e("asd", condition.toString())
        Log.e("asd", distance.toString())
        Log.e("asd", shipping.toString())

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.recycleView.layoutManager = GridLayoutManager(this@SearchResultActivity, 2)
        binding.recycleView.adapter = mAdapter

        mAdapter.onCartPlusListener = {
            val imageView: ImageView? =
                binding.recycleView.findViewHolderForLayoutPosition(it)?.itemView?.findViewById(
                    R.id.cart_operation
                )
            imageView?.isEnabled = false
            val productsInfo = mAdapter.getItem(it)
            val item = Gson().toJson(productsInfo)
            lifecycleScope.launch {
                try {
                    apiService.add(item = item)
                    //success
                    Toast.makeText(
                        this@SearchResultActivity,
                        "${productsInfo.title?.get(0)?.substring(0, 10)}... was added to wishlist",
                        Toast.LENGTH_SHORT
                    ).show()
                    LiveDataEventBus.instance.postCartOperation(
                        CartOperationEvent(
                            true,
                            productsInfo.itemId?.get(0)
                        )
                    )
                    imageView?.isEnabled = true
                    imageView?.setImageResource(R.drawable.ic_cart_remove)
                    mAdapter.getItem(it).isCollected = true
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    imageView?.isEnabled = true
                }
            }
        }

        mAdapter.onCartRemoveListener = {
            val imageView: ImageView? =
                binding.recycleView.findViewHolderForLayoutPosition(it)?.itemView?.findViewById(
                    R.id.cart_operation
                )
            imageView?.isEnabled = false
            val productsInfo = mAdapter.getItem(it)
            val item = Gson().toJson(productsInfo)
            lifecycleScope.launch {
                try {
                    apiService.del(item = item)
                    //success
                    Toast.makeText(
                        this@SearchResultActivity,
                        "${
                            productsInfo.title?.get(0)?.substring(0, 10)
                        }... was removed from wishlist",
                        Toast.LENGTH_SHORT
                    ).show()
                    LiveDataEventBus.instance.postCartOperation(
                        CartOperationEvent(
                            false,
                            productsInfo.itemId?.get(0)
                        )
                    )
                    imageView?.isEnabled = true
                    imageView?.setImageResource(R.drawable.ic_cart_plus)
                    mAdapter.getItem(it).isCollected = false
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@SearchResultActivity,
                        "Fetch Error Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    imageView?.isEnabled = true
                }
            }
        }

        mAdapter.onItemClickListener = {
            val productsInfo = mAdapter.getItem(it)
            val item = Gson().toJson(productsInfo)
            startProductDetail(item)
        }

        //查找刷新按钮状态
        LiveDataEventBus.instance.cartOperationData.observe(this) { event ->
            mAdapter.getData().find { productInfo ->
                productInfo.itemId?.get(0) == event.itemId
            }?.let { productInfo ->
                productInfo.isCollected = event.add
                mAdapter.getData().indexOf(productInfo).takeIf { index ->
                    index > -1
                }?.let { position ->
                    val imageView: ImageView? =
                        binding.recycleView.findViewHolderForLayoutPosition(position)?.itemView?.findViewById(
                            R.id.cart_operation
                        )
                    if (event.add) {
                        imageView?.setImageResource(R.drawable.ic_cart_remove)
                    } else {
                        imageView?.setImageResource(R.drawable.ic_cart_plus)
                    }
                }
            }
        }

        search()
    }

    private fun search() {
        lifecycleScope.launch {
            try {
                //先请求收藏
                val response = apiService.queryFavorites()
                val favoriteList = response.productsInfo ?: listOf()

                val queryMap = mutableMapOf<String, String>()
                queryMap["keyword"] = keyword
                queryMap["category"] = category.toString()
                if (!currentLocation.isNullOrBlank()) {
                    queryMap["currentLocation"] = currentLocation!!
                }
                if (!distance.isNullOrBlank()) {
                    queryMap["distance"] = distance!!
                }
                if (!condition.isNullOrBlank()) {
                    queryMap["condition"] = condition!!
                }
                if (!shipping.isNullOrBlank()) {
                    queryMap["shipping"] = shipping!!
                }
                val baseResponse = apiService.search(queryMap)
                binding.recycleView.visible()

                if (baseResponse.productsInfo.isNullOrEmpty()) {
                    Toast.makeText(this@SearchResultActivity, "Empty Data", Toast.LENGTH_SHORT)
                        .show()
                    return@launch
                }

                mAdapter.setNewData(baseResponse.productsInfo?.map { item ->
                    item.also {
                        item.isCollected = favoriteList.find {
                            item.itemId == it.itemId
                        } != null
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@SearchResultActivity,
                    "Search Error please try again",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.progress.gone()
            }
        }
    }
}