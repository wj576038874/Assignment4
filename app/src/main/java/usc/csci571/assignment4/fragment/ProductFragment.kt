package usc.csci571.assignment4.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import usc.csci571.assignment4.adapter.ItemSpecificsAdapter
import usc.csci571.assignment4.adapter.PictureAdapter
import usc.csci571.assignment4.databinding.FragmnetProductBinding
import usc.csci571.assignment4.databinding.SearchBinding
import usc.csci571.assignment4.viewmodel.InteractionViewModel

/**
 * author: wenjie
 * date: 2023/12/6 10:07
 * description:
 */
class ProductFragment : Fragment() {

    private var _binding: FragmnetProductBinding? = null

    private val binding get() = _binding!!

    private val viewModel by activityViewModels<InteractionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmnetProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.productDetailData.observe(viewLifecycleOwner) { productDetail ->
            val itemDetails = productDetail.itemDetails
            val pictureList = itemDetails?.PictureURL
            val pictureAdapter = PictureAdapter(pictureList ?: listOf())
            binding.viewPager2.adapter = pictureAdapter
            binding.tvTitle.text = itemDetails?.Title
            binding.tvPriceWithShipping.text =
                "$ ${itemDetails?.CurrentPrice?.value} with Free Shipping"
            binding.tvPrice.text = "$ ${itemDetails?.CurrentPrice?.value}"
            binding.tvBrand.text = "${
                itemDetails?.ItemSpecifics?.NameValueList?.find {
                    it.Name == "Brand"
                }?.Value?.get(0)
            }"

            val specs = itemDetails?.ItemSpecifics?.NameValueList?.flatMap {
                it.Value ?: listOf()
            }
            val itemSpecificsAdapter = ItemSpecificsAdapter(specs ?: listOf())
            binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
            binding.recycleView.adapter = itemSpecificsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): ProductFragment = ProductFragment()
    }
}