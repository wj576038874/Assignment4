package usc.csci571.assignment4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import usc.csci571.assignment4.adapter.ProductListAdapter
import usc.csci571.assignment4.bean.ProductsInfo
import usc.csci571.assignment4.databinding.ActivitySearchResultBinding
import usc.csci571.assignment4.http.ApiService
import usc.csci571.assignment4.http.RetrofitHelper

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

    private var favoriteList = listOf<ProductsInfo>()

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
                    imageView?.isEnabled = true
                    imageView?.setImageResource(R.drawable.ic_cart_plus)
                    mAdapter.getItem(it).isCollected = false
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    imageView?.isEnabled = true
                }
            }
        }

        mAdapter.onItemClickListener = {
            val productsInfo = mAdapter.getItem(it)
            startActivity(Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("itemTitle", productsInfo.title?.get(0))
                putExtra("itemId", productsInfo.itemId?.get(0))
            })
        }

        search()
    }

    private fun search() {
        lifecycleScope.launch {
            try {
                //先请求收藏
                val response = apiService.queryFavorites()
                favoriteList = response.productsInfo ?: listOf()

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