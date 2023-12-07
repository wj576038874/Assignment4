package usc.csci571.assignment4.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import usc.csci571.assignment4.bean.SimilarItem
import usc.csci571.assignment4.databinding.SimilarItemBinding

/**
 * author: wenjie
 * date: 2023/12/6 14:02
 * description:
 */
class SimilarItemAdapter :
    RecyclerView.Adapter<SimilarItemAdapter.SimilarItemHolder>() {

    class SimilarItemHolder(val binding: SimilarItemBinding) : RecyclerView.ViewHolder(binding.root)

    var data = listOf<SimilarItem>()

    fun setNewData(data: List<SimilarItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarItemHolder {
        return SimilarItemHolder(
            SimilarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SimilarItemHolder, position: Int) {
        val similarItem = data[position]
        Glide.with(holder.itemView)
            .load(similarItem.imageURL)
            .into(holder.binding.cover)
        holder.binding.tvTitle.text = similarItem.title
        holder.binding.tvPrice.text = "$${similarItem.buyItNowPrice?.value}"
        //P8DT14H50M43S
        holder.binding.tvTimeLeft.text =
            if (similarItem.timeLeftInt > 0) "${similarItem.timeLeftInt} Days Left" else "${similarItem.timeLeftInt} Day Left"

        holder.binding.tvShipping.text =
            if (similarItem.shippingCost?.value == "0.00") "Free shipping" else similarItem.shippingCost?.value
    }
}