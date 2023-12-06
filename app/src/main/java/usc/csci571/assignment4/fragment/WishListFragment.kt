package usc.csci571.assignment4.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import usc.csci571.assignment4.ProductDetailActivity
import usc.csci571.assignment4.R
import usc.csci571.assignment4.adapter.ProductListAdapter
import usc.csci571.assignment4.databinding.WishListBinding
import usc.csci571.assignment4.gone
import usc.csci571.assignment4.http.ApiService
import usc.csci571.assignment4.http.RetrofitHelper
import usc.csci571.assignment4.viewmodel.InteractionViewModel
import usc.csci571.assignment4.viewmodel.RefreshWishEventBus
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

        RefreshWishEventBus.instance.cartOperationData.observe(viewLifecycleOwner) {
            //有添加心愿单操作 就刷新心愿单列表
            queryFavorites()
            Toast.makeText(context, "queryFavorites", Toast.LENGTH_SHORT).show()
        }

        mAdapter.onItemClickListener = {
            val productsInfo = mAdapter.getItem(it)
            startActivity(Intent(requireContext(), ProductDetailActivity::class.java).apply {
                putExtra("itemTitle", productsInfo.title?.get(0))
                putExtra("itemId", productsInfo.itemId?.get(0))
                putExtra("isCollected", productsInfo.isCollected)
            })
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
                val baseResponse = apiService.queryFavorites()
                if (baseResponse.productsInfo.isNullOrEmpty()) {
                    binding.recycleView.gone()
                    binding.cardEmpty.visible()
                } else {
                    binding.recycleView.visible()
                    binding.cardEmpty.gone()
                    mAdapter.setNewData(baseResponse.productsInfo.onEach {
                        it.isCollected = true
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Fetch Error Please Try Again",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.progress.gone()
                isLoad = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        var isRefresh = false

        fun newInstance(): WishListFragment = WishListFragment()
    }
}