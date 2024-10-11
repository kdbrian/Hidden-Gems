package io.github.junrdev.hiddengems.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.databinding.BarnercarrouselimagecardBinding

class ImagesListAdapter(
    private val context: Context,
    val images : List<String>
) : RecyclerView.Adapter<ImagesListAdapter.VH>(){


    inner class VH(private val binding : BarnercarrouselimagecardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindItem(x: String) {
            binding.apply {
                Glide.with(context)
                    .load(x)
                    .centerCrop()
                    .into(imageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.barnercarrouselimagecard, parent, false)
        return VH(BarnercarrouselimagecardBinding.bind(view))
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindItem(images[position])
    }

}