package usc.csci571.assignment4.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import usc.csci571.assignment4.R

/**
 * author: wenjie
 * date: 2023/12/6 11:56
 * description:
 */
class PictureAdapter(val data: List<String>) : RecyclerView.Adapter<PictureAdapter.ImageHolder>() {

    class ImageHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.image)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.picture_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Glide.with(holder.imageView)
            .load(data[position])
            .centerCrop()
            .into(holder.imageView)
    }
}