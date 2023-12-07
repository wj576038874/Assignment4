package usc.csci571.assignment4.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import usc.csci571.assignment4.ProductDetailActivity.Companion.startProductDetail
import usc.csci571.assignment4.R
import usc.csci571.assignment4.adapter.ProductListAdapter
import usc.csci571.assignment4.databinding.WishListBinding
import usc.csci571.assignment4.gone
import usc.csci571.assignment4.http.ApiService
import usc.csci571.assignment4.http.RetrofitHelper
import usc.csci571.assignment4.viewmodel.LiveDataEventBus
import usc.csci571.assignment4.visible

/**
 * author: wenjie
 * date: 2023/12/5 15:49
 * description:
 */
class WishListFragment : Fragment() {

    private var _binding: WishListBinding? = null

    private val binding get() = _binding!!

    private var isLoad = false

    private val apiService by lazy(LazyThreadSafetyMode.NONE) {
        RetrofitHelper.getRetrofit().create(ApiService::class.java)
    }

    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ProductListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WishListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleView.adapter = mAdapter

        LiveDataEventBus.instance.cartOperationData.observe(viewLifecycleOwner) {
            //有添加心愿单操作 就刷新心愿单列表
            if (isLoad) {
                queryFavorites()
            }
        }

        mAdapter.onItemClickListener = {
            val productsInfo = mAdapter.getItem(it)
            val item = Gson().toJson(productsInfo)
            startProductDetail(item)
        }

        mAdapter.onCartRemoveListener = { position ->
            val imageView: ImageView? =
                binding.recycleView.findViewHolderForLayoutPosition(position)?.itemView?.findViewById(
                    R.id.cart_operation
                )
            imageView?.isEnabled = false
            val productsInfo = mAdapter.getItem(position)
            val item = Gson().toJson(productsInfo)
            lifecycleScope.launch {
                try {
                    apiService.del(item = item)
                    //success
                    Toast.makeText(
                        requireContext(),
                        "${
                            productsInfo.title?.get(0)?.substring(0, 10)
                        }... was removed from wishlist",
                        Toast.LENGTH_SHORT
                    ).show()
                    imageView?.isEnabled = true
                    mAdapter.notifyRemove(position)
                    if (mAdapter.getData().isEmpty()) {
                        binding.recycleView.gone()
                        binding.cardEmpty.visible()
                    }
                    refreshTotalView()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        "Fetch Error Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    imageView?.isEnabled = true
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (!isLoad) {
            queryFavorites()
        }
    }

    private fun queryFavorites() {
        lifecycleScope.launch {
            try {
                val response = apiService.queryFavorites()
                if (response.productsInfo.isNullOrEmpty()) {
                    binding.recycleView.gone()
                    binding.llTotal.gone()
                    binding.cardEmpty.visible()
                } else {
                    binding.llTotal.visible()
                    binding.recycleView.visible()
                    binding.cardEmpty.gone()
                    mAdapter.setNewData(response.productsInfo.onEach {
                        it.isCollected = true
                    })
                    refreshTotalView()
                }
                isLoad = true
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Fetch Error Please Try Again",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.progress.gone()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshTotalView() {
        if (mAdapter.getData().isEmpty()) {
            binding.llTotal.gone()
        } else {
            binding.llTotal.visible()
        }
        val totalPrice = mAdapter.getData().sumOf {
            it.sellingStatus?.get(0)?.currentPrice?.get(0)?.value?.toBigDecimal()
                ?: 0.toBigDecimal()
        }
        binding.tvTotal.text = "Wishlist Total(${mAdapter.getData().size} items)"
        binding.tvPrice.text = "$$totalPrice"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): WishListFragment = WishListFragment()
    }
}