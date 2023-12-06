package usc.csci571.assignment4.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import usc.csci571.assignment4.SearchResultActivity
import usc.csci571.assignment4.databinding.SearchBinding
import usc.csci571.assignment4.gone
import usc.csci571.assignment4.http.ApiService
import usc.csci571.assignment4.http.OkHttpHelper
import usc.csci571.assignment4.http.RetrofitHelper
import usc.csci571.assignment4.visible
import java.io.IOException

/**
 * author: wenjie
 * date: 2023/12/5 15:45
 * description:
 */
class SearchFragment : Fragment() {

    private var _binding: SearchBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }


    //搜索
    private fun search() {
        val keyword = binding.keywordInput.text?.toString() ?: ""
        val category = getCategory()
        val currentLocation = "30022"
        val distance = if (binding.distanceInput.text?.toString().isNullOrBlank()){
            "10"
        }else{
            binding.distanceInput.text.toString()
        }
        val conditions = mutableListOf<String>()
        //New,Used,Unspecified
        if (binding.conditionNew.isChecked) {
            conditions += "New"
        }
        if (binding.conditionUsed.isChecked) {
            conditions += "Used"
        }
        if (binding.conditionUnspecified.isChecked) {
            conditions += "Unspecified"
        }
        val condition = conditions.joinToString(",")
        Log.e("condition", condition)
        val shipping = if (binding.shippingPkup.isChecked && binding.shippingFree.isChecked) {
            "LocalPickupOnly,FreeShippingOnly"
        } else if (binding.shippingPkup.isChecked) {
            "LocalPickupOnly"
        } else if (binding.shippingFree.isChecked) {
            "FreeShippingOnly"
        } else {
            null
        }
        val intent = Intent(requireContext(), SearchResultActivity::class.java)
        intent.putExtra("keyword", keyword)
        intent.putExtra("category", category)
        intent.putExtra("currentLocation", currentLocation)
        intent.putExtra("distance", distance)
        intent.putExtra("condition", condition)
        intent.putExtra("shipping", shipping)
        startActivity(intent)
//        try {
//            lifecycleScope.launch {
//                val keyword = "iphone"
//                val category = getCategory()
//                val currentLocation = "30022"
//                val distance = "12"
//                val condition = "Used"
//                val shipping = "FreeShippingOnly"
//                val response = RetrofitHelper.getRetrofit().create(ApiService::class.java)
//                    .search(
//                        keyword = "iphone",
//                        category = category, currentLocation = currentLocation,
//                        distance = distance,
//                        condition = condition,
//                        shipping = shipping
//                    )
////                val urlBuilder = HttpUrl.get("https://csci571-xm-assignment3.wl.r.appspot.com/productsQuery").newBuilder()
////                urlBuilder.addQueryParameter("keyword" , keyword)
////                urlBuilder.addQueryParameter("category",category.toString())
////                urlBuilder.addQueryParameter("currentLocation",currentLocation)
////                urlBuilder.addQueryParameter("distance",distance)
////                urlBuilder.addQueryParameter("condition",condition)
////                urlBuilder.addQueryParameter("shipping",shipping)
////                val request: Request = Request.Builder()
////                    .url(urlBuilder.toString())
////                    .build()
////                val call = OkHttpHelper.instance.getOkHttpClient().newCall(request)
////                call.enqueue(object : Callback{
////                    override fun onFailure(call: Call, e: IOException) {
////
////                    }
////
////                    override fun onResponse(call: Call, response: Response) {
////                        Log.e("asd" , response.body()?.string().toString())
////                    }
////                })
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(activity, "请求失败", Toast.LENGTH_SHORT)
//                .show()
//        }
    }

    private fun getCategory(): Int {
        return when (binding.category.selectedItemPosition) {
            1 -> 550
            2 -> 2984
            3 -> 267
            4 -> 11450
            5 -> 58058
            6 -> 26395
            7 -> 11233
            8 -> 1249
            else -> 0
        }
    }

    /**
     * 事件监听
     */
    private fun initListener() {
        binding.submitBtn.setOnClickListener {
            if (binding.keywordInput.text?.toString()?.trim().isNullOrBlank()) {
                binding.keywordAlert.visible()
                return@setOnClickListener
            }
            binding.keywordAlert.gone()

            if (binding.enableNearbySearch.isChecked) {
                //选中定位
                if (binding.radioInputLocation.isChecked) {
                    if (binding.zipcodeInput.text?.toString()?.trim().isNullOrBlank()) {
                        binding.zipcodeAlert.visible()
                        return@setOnClickListener
                    }
                    binding.zipcodeAlert.gone()
                }
            }

            search()
        }

        binding.clearBtn.setOnClickListener {
            binding.keywordAlert.gone()
            binding.category.setSelection(0)
            binding.conditionNew.isChecked = false
            binding.conditionUnspecified.isChecked = false
            binding.conditionUsed.isChecked = false
            binding.shippingPkup.isChecked = false
            binding.shippingFree.isChecked = false
            binding.distanceInput.text = null
            binding.zipcodeInput.text = null
        }

        binding.keywordInput.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            if (binding.keywordInput.text?.toString()?.trim().isNullOrBlank()) {
                binding.keywordAlert.visible()
            } else {
                binding.keywordAlert.gone()
            }
        })

        binding.enableNearbySearch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.LocationLayout.visible()
            } else {
                binding.LocationLayout.gone()
            }
        }

        binding.radioCurLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.radioInputLocation.isChecked = !isChecked
        }
        binding.radioInputLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.radioCurLocation.isChecked = !isChecked
            binding.zipcodeInput.isEnabled = isChecked
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): SearchFragment = SearchFragment()
    }
}