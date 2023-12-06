package usc.csci571.assignment4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import usc.csci571.assignment4.bean.ProductDetail

/**
 * author: wenjie
 * date: 2023/12/6 11:25
 * description:
 */
class InteractionViewModel : ViewModel() {

    private val _productDetailData = MutableLiveData<ProductDetail>()
    val productDetailData: LiveData<ProductDetail>
        get() = _productDetailData


    fun post(detail: ProductDetail) {
        _productDetailData.value = detail
    }
}