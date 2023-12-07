package usc.csci571.assignment4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * author: wenjie
 * date: 2023/12/6 18:38
 * description: 列表添加和删除心愿单成功之后 通知心愿清单页面刷新 心愿清单数据
 * 详情添加或者删除 搜索页面需要同步状态
 */
class LiveDataEventBus private constructor() {

    companion object {
        val instance by lazy {
            LiveDataEventBus()
        }
    }

    private val _cartOperationData = MutableLiveData<CartOperationEvent>()
    val cartOperationData: LiveData<CartOperationEvent>
        get() = _cartOperationData


    fun postCartOperation(event: CartOperationEvent) {
        _cartOperationData.value = event
    }

}

data class CartOperationEvent(
    val add: Boolean,//true为添加 false为删除
    val itemId: String?
)