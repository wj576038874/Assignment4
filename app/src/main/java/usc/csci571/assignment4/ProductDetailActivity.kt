package usc.csci571.assignment4

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import usc.csci571.assignment4.adapter.ProductDetailPageAdapter
import usc.csci571.assignment4.databinding.ActivityProductDetailBinding
import usc.csci571.assignment4.fragment.PhotoFragment
import usc.csci571.assignment4.fragment.ProductFragment
import usc.csci571.assignment4.fragment.ShippingFragment
import usc.csci571.assignment4.fragment.SimilarFragment
import usc.csci571.assignment4.http.ApiService
import usc.csci571.assignment4.http.RetrofitHelper
import usc.csci571.assignment4.viewmodel.InteractionViewModel

/**
 * author: wenjie
 * date: 2023/12/6 09:49
 * description:
 */
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    private var itemId: String? = null
    private var itemTitle: String? = null

    private val tabs = listOf(
        "PRODUCT" to R.drawable.information_variant_selected,
        "SHIPPING" to R.drawable.truck_delivery_selected,
        "PHOTOS" to R.drawable.google_selected,
        "SIMILAR" to R.drawable.equal_selected
    )

    private val viewModel by viewModels<InteractionViewModel>()

    private val pagerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ProductDetailPageAdapter(
            listOf(
                ProductFragment.newInstance(),
                ShippingFragment.newInstance(),
                PhotoFragment.newInstance(),
                SimilarFragment.newInstance(),
            ),
            this
        )
    }

    private val apiService by lazy(LazyThreadSafetyMode.NONE) {
        RetrofitHelper.getRetrofit().create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        itemId = intent?.getStringExtra("itemId")
        itemTitle = intent?.getStringExtra("itemTitle")
        binding.toolbar.title = itemTitle
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.viewPager2.adapter = pagerAdapter

        binding.btnPlus.setOnClickListener {

        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = tabs[position].first
            tab.setIcon(tabs[position].second)
        }.attach()

        lifecycleScope.launch {
            try {
                if (itemId.isNullOrBlank() || itemTitle.isNullOrBlank()) {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Wrong Parameter",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                val productResponse = apiService.productDetailQuery(itemId!!, itemTitle!!)
                viewModel.post(productResponse)
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