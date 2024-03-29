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
            binding.title.text = productsInfo.title?.get(0)
            binding.zipcode.text = "Zip:${productsInfo.postalCode?.get(0)}"
            binding.shippingType.text =
                productsInfo.shippingInfo?.get(0)?.shippingType?.get(0)
            binding.condition.text =
                productsInfo.condition?.get(0)?.conditionDisplayName?.get(0)
            binding.price.text = "$${productsInfo.sellingStatus?.get(0)?.currentPrice?.get(0)?.value}"

            if (productsInfo.isCollected) {
                binding.cartOperation.setImageResource(R.drawable.ic_cart_remove)
            } else {
                binding.cartOperation.setImageResource(R.drawable.ic_cart_plus)
            }

            binding.cartOperation.setOnClickListener {
                if (productsInfo.isCollected) {
                    onCartRemoveListener?.invoke(holder.adapterPosition)
                } else {
                    onCartPlusListener?.invoke(holder.adapterPosition)
                }
            }

            itemView.setOnClickListener {
                onItemClickListener?.invoke(holder.adapterPosition)
            }
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

    class SearchViewHolder(val binding: ProductCardBinding) : RecyclerView.ViewHolder(binding.root)

}