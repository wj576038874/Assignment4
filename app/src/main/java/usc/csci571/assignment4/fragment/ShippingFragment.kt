package usc.csci571.assignment4.fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import usc.csci571.assignment4.R
import usc.csci571.assignment4.databinding.FragmentShippingBinding
import usc.csci571.assignment4.viewmodel.InteractionViewModel

/**
 * author: wenjie
 * date: 2023/12/6 10:07
 * description:
 */
class ShippingFragment : Fragment() {

    private var _binding: FragmentShippingBinding? = null

    private val binding get() = _binding!!

    private val viewModel by activityViewModels<InteractionViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.productInfoData.observe(viewLifecycleOwner) {
            val shipping = it.shippingInfo?.get(0)?.shippingServiceCost?.get(0)?.value
            binding.shippingCost.text = if (shipping == "0.0") "Free" else shipping
        }

        viewModel.productDetailData.observe(viewLifecycleOwner) {
            val url = it.itemDetails?.Storefront?.StoreURL
            val storeName = it.itemDetails?.Storefront?.StoreName.toString()
            val spannableString = SpannableString(storeName)
            val urlSpan = URLSpan(url)
            spannableString.setSpan(urlSpan, 0, storeName.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            binding.tvStoreName.text = spannableString

            binding.tvFeedbackScore.text = it.itemDetails?.Seller?.FeedbackScore

            val popularity = it.itemDetails?.Seller?.PositiveFeedbackPercent
            binding.popularity.score = popularity ?: 0

            binding.ivStar.setImageResource(R.drawable.star)


            binding.tvGlobalShipping.text =
                if (it.itemDetails?.GlobalShipping == true) "YES" else "No"

            binding.tvHandlingTime.text = it.itemDetails?.HandlingTime.toString()

            binding.tvPolicy.text = it.itemDetails?.ReturnPolicy?.ReturnsAccepted

            binding.tvReturnsWithin.text = it.itemDetails?.ReturnPolicy?.ReturnsWithin

            binding.tvRefund.text = it.itemDetails?.ReturnPolicy?.Refund

            binding.tvShippingCostPaidBy.text = it.itemDetails?.ReturnPolicy?.ShippingCostPaidBy
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(param: String?): ShippingFragment = ShippingFragment().also {
            it.arguments = Bundle().apply {
                putString("param", param)
            }
        }
    }
}