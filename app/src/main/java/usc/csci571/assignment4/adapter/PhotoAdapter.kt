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
 * date: 2023/12/6 13:46
 * description:
 */
class PhotoAdapter(private val data: List<String>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    class PhotoHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        return PhotoHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        Glide.with(holder.imageView)
            .load(data[position])
            .centerCrop()
            .into(holder.imageView)
    }
}