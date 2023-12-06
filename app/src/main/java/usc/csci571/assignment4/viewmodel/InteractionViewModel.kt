package usc.csci571.assignment4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import usc.csci571.assignment4.bean.ProductDetail
import usc.csci571.assignment4.bean.ProductsInfo

/**
 * author: wenjie
 * date: 2023/12/6 11:25
 * description:
 */
class InteractionViewModel : ViewModel() {

    private val _productDetailData = MutableLiveData<ProductDetail>()
    val productDetailData: LiveData<ProductDetail>
        get() = _productDetailData


    private val _productInfoData = MutableLiveData<ProductsInfo>()
    val productInfoData: LiveData<ProductsInfo>
        get() = _productInfoData

    fun postDetail(detail: ProductDetail) {
        _productDetailData.value = detail
    }

    fun postInfo(productsInfo: ProductsInfo) {
        _productInfoData.value = productsInfo
    }
}