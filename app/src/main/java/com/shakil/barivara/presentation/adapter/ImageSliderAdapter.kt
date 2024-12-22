package com.shakil.barivara.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.R
import com.shakil.barivara.data.model.SliderItem

class ImageSliderAdapter(
    private val context: Context,
    private val imageUrls: List<SliderItem>
) : RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adaptter_layout_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val imageUrl: Int = imageUrls[position].imageDrawable
        holder.imageView.background = ContextCompat.getDrawable(holder.itemView.context, imageUrl)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    fun getSliders(): List<SliderItem> {
        return imageUrls
    }

    class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}
