package usc.csci571.assignment4.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import usc.csci571.assignment4.R
import usc.csci571.assignment4.bean.ProductsInfo
import usc.csci571.assignment4.databinding.ProductCardBinding

/**
 * author: wenjie
 * date: 2023/12/5 21:14
 * description:
 */

typealias OnCartRemoveListener = (Int) -> Unit
typealias OnCartPlusListener = (Int) -> Unit

typealias OnItemClickListener = (Int) -> Unit

class ProductListAdapter : RecyclerView.Adapter<ProductListAdapter.SearchViewHolder>() {

    private var mData = mutableListOf<ProductsInfo>()

    var onCartRemoveListener: OnCartRemoveListener? = null
    var onCartPlusListener: OnCartPlusListener? = null
    var onItemClickListener: OnItemClickListener? = null

    fun setNewData(data: List<ProductsInfo>?) {
        this.mData = data?.toMutableList() ?: mutableListOf()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val productsInfo = mData[position]

        Glide.with(holder.binding.cover)
            .load(productsInfo.galleryURL?.get(0))
            .into(holder.binding.cover)
        holder.apply {
            holder.binding.title.text = productsInfo.title?.get(0)
            holder.binding.zipcode.text = "Zip:${productsInfo.postalCode?.get(0)}"
            holder.binding.shippingType.text =
                productsInfo.shippingInfo?.get(0)?.shippingType?.get(0)
            holder.binding.condition.text =
                productsInfo.condition?.get(0)?.conditionDisplayName?.get(0)
            holder.binding.price.text =
                productsInfo.sellingStatus?.get(0)?.currentPrice?.get(0)?.value
        }

        if (productsInfo.isCollected) {
            holder.binding.cartOperation.setImageResource(R.drawable.ic_cart_remove)
        } else {
            holder.binding.cartOperation.setImageResource(R.drawable.ic_cart_plus)
        }

        holder.binding.cartOperation.setOnClickListener {
            if (productsInfo.isCollected) {
                onCartRemoveListener?.invoke(holder.adapterPosition)
            } else {
                onCartPlusListener?.invoke(holder.adapterPosition)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(holder.adapterPosition)
        }
    }

    fun getItem(position: Int): ProductsInfo {
        return mData[position]
    }

    fun getData() = mData

    fun notifyRemove(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    class SearchViewHolder(val binding: ProductCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        val cover: ImageView = view.findViewById(R.id.cover)
//        val title: TextView = view.findViewById(R.id.title)
//        val zipcode: TextView = view.findViewById(R.id.zipcode)
//        val shipping: TextView = view.findViewById(R.id.shipping_type)
//        val condition: TextView = view.findViewById(R.id.condition)
//        val price: TextView = view.findViewById(R.id.price)
//        val cartOperation: ImageView = view.findViewById(R.id.cart_operation)
    }

}