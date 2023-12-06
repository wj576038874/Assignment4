package usc.csci571.assignment4.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import usc.csci571.assignment4.adapter.ProductListAdapter
import usc.csci571.assignment4.databinding.WishListBinding
import usc.csci571.assignment4.gone
import usc.csci571.assignment4.http.ApiService
import usc.csci571.assignment4.http.RetrofitHelper
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
    }

    override fun onResume() {
        super.onResume()
        if (!isLoad) {
            lifecycleScope.launch {
                try {
                    val baseResponse = apiService.queryFavorites()

                    if (baseResponse.productsInfo.isNullOrEmpty()) {
                        binding.recycleView.gone()
                        binding.cardEmpty.visible()
                    } else {
                        binding.recycleView.visible()
                        binding.cardEmpty.gone()
                        mAdapter.setNewData(baseResponse.productsInfo, baseResponse.productsInfo)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): WishListFragment = WishListFragment()
    }
}