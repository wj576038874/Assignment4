package usc.csci571.assignment4

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.coroutines.launch
import usc.csci571.assignment4.adapter.ProductDetailPageAdapter
import usc.csci571.assignment4.bean.ProductsInfo
import usc.csci571.assignment4.databinding.ActivityProductDetailBinding
import usc.csci571.assignment4.fragment.PhotoFragment
import usc.csci571.assignment4.fragment.ProductFragment
import usc.csci571.assignment4.fragment.ShippingFragment
import usc.csci571.assignment4.fragment.SimilarFragment
import usc.csci571.assignment4.http.ApiService
import usc.csci571.assignment4.http.RetrofitHelper
import usc.csci571.assignment4.viewmodel.InteractionViewModel
import usc.csci571.assignment4.viewmodel.RefreshWishEventBus

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

        binding.toolbar.title = productsInfo?.title.toString()
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        pagerAdapter = ProductDetailPageAdapter(
            listOf(
                //运费传递进去
                ProductFragment.newInstance(productsInfo?.shippingInfo?.get(0)?.shippingServiceCost?.get(0)?.value),
                ShippingFragment.newInstance(productsInfo?.shippingInfo?.get(0)?.shippingServiceCost?.get(0)?.value),
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
                        RefreshWishEventBus.instance.postCartOperation(false)
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
                        RefreshWishEventBus.instance.postCartOperation(false)
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
                val productResponse = apiService.productDetailQuery(itemId, itemTitle)
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

}