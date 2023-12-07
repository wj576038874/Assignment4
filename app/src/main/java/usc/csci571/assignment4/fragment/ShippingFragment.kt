package usc.csci571.assignment4.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import usc.csci571.assignment4.R
import usc.csci571.assignment4.databinding.FragmentShippingBinding
import usc.csci571.assignment4.gone
import usc.csci571.assignment4.viewmodel.InteractionViewModel
import usc.csci571.assignment4.visible

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

        val shipping = arguments?.getString("param", null)

        viewModel.productDetailData.observe(viewLifecycleOwner) {
            //storeName
            val storeName = it.itemDetails?.Storefront?.StoreName
            if (!storeName.isNullOrBlank()) {
                binding.llStoreName.visible()
                val url = it.itemDetails.Storefront.StoreURL
                val spannableString = SpannableString(storeName)
                val urlSpan = URLSpan(url)
                spannableString.setSpan(
                    urlSpan,
                    0,
                    storeName.length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
                binding.tvStoreName.text = spannableString
                binding.tvStoreName.movementMethod = LinkMovementMethod.getInstance()
            } else {
                binding.llStoreName.gone()
            }

            //feedbackScore
            val feedbackScore = it.itemDetails?.Seller?.FeedbackScore
            if (!feedbackScore.isNullOrBlank()) {
                binding.llFeedbackScore.visible()
                binding.tvFeedbackScore.text = feedbackScore
            } else {
                binding.llFeedbackScore.gone()
            }

            //popularity
            val popularity = it.itemDetails?.Seller?.PositiveFeedbackPercent
            if (popularity != null) {
                binding.llPopularity.visible()
                binding.popularity.score = popularity.toInt()
            } else {
                binding.llPopularity.gone()
            }

            //FeedbackRatingStar
            binding.ivStar.setImageResource(R.drawable.star_circle)
            when (it.itemDetails?.Seller?.FeedbackRatingStar) {
                "None" -> {
                    binding.ivStar.imageTintList = ColorStateList.valueOf(Color.BLACK)
                }

                "Yellow" -> {
                    binding.ivStar.imageTintList = ColorStateList.valueOf(Color.YELLOW)
                }

                "Purple" -> {
                    binding.ivStar.imageTintList =
                        ColorStateList.valueOf(Color.parseColor("#512DA8"))
                }

                "Red" -> {
                    binding.ivStar.imageTintList = ColorStateList.valueOf(Color.RED)
                }

                "Blue" -> {
                    binding.ivStar.imageTintList = ColorStateList.valueOf(Color.BLUE)
                }

                "Turquoise" -> {
                    //
                }

                "Green" -> {
                    binding.ivStar.imageTintList = ColorStateList.valueOf(Color.GREEN)
                }
            }

            //shippingCost
            binding.shippingCost.text = if (shipping == "0.0") "Free" else shipping

            //GlobalShipping
            binding.tvGlobalShipping.text =
                if (it.itemDetails?.GlobalShipping == true) "YES" else "No"

            //HandlingTime
            binding.tvHandlingTime.text = it.itemDetails?.HandlingTime.toString()

            //policy
            val policy = it.itemDetails?.ReturnPolicy?.ReturnsAccepted
            if (!policy.isNullOrBlank()) {
                binding.llPopularity.visible()
                binding.tvPolicy.text = policy
            } else {
                binding.llPolicy.gone()
            }

            //ReturnsWithin
            val returnsWithin = it.itemDetails?.ReturnPolicy?.ReturnsWithin
            if (!returnsWithin.isNullOrBlank()) {
                binding.llReturnsWithin.visible()
                binding.tvReturnsWithin.text = it.itemDetails?.ReturnPolicy?.ReturnsWithin
            } else {
                binding.llReturnsWithin.gone()
            }

            //Refund
            val refund = it.itemDetails?.ReturnPolicy?.Refund
            if (!refund.isNullOrBlank()) {
                binding.llRefund.visible()
                binding.tvRefund.text = refund
            } else {
                binding.llRefund.gone()
            }

            //ShippingCostPaidBy
            val shippingCostPaidBy = it.itemDetails?.ReturnPolicy?.ShippingCostPaidBy
            if (!shippingCostPaidBy.isNullOrBlank()) {
                binding.llShippingby.visible()
                binding.tvShippingCostPaidBy.text = shippingCostPaidBy
            } else {
                binding.llShippingby.gone()
            }
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