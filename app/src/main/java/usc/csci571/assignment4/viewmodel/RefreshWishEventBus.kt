package usc.csci571.assignment4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * author: wenjie
 * date: 2023/12/6 18:38
 * description: 列表添加和删除心愿单成功之后 通知心愿清单页面刷新 心愿清单数据
 */
class RefreshWishEventBus private constructor() {

    companion object {
        val instance by lazy {
            RefreshWishEventBus()
        }
    }

    private val _cartOperationData = MutableLiveData<Boolean>()
    val cartOperationData: LiveData<Boolean>
        get() = _cartOperationData

    fun postCartOperation(add: Boolean) {
        _cartOperationData.value = add
    }

}