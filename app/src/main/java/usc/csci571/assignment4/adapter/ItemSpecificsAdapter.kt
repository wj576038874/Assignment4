package usc.csci571.assignment4.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import usc.csci571.assignment4.R

/**
 * author: wenjie
 * date: 2023/12/6 12:57
 * description:
 */
class ItemSpecificsAdapter(private val specs: List<String>) :
    RecyclerView.Adapter<ItemSpecificsAdapter.ItemSpecificsHolder>() {

    class ItemSpecificsHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSpecificsHolder {
        return ItemSpecificsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_spec_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return specs.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemSpecificsHolder, position: Int) {
        (holder.itemView as TextView).text = "·${specs[position]}"
    }
}