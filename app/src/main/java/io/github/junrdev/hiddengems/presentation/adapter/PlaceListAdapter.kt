package io.github.junrdev.hiddengems.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.Gem
import io.github.junrdev.hiddengems.databinding.PlacepreviewBinding

class PlaceListAdapter(
    val context: Context,
    val places: List<Gem>,
    val onclick: (gem: Gem) -> Unit
) : RecyclerView.Adapter<PlaceListAdapter.VH>() {

    inner class VH(val binding: PlacepreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(x: Gem) {
            binding.apply {
                gem = x
                textView5
                if (x.images.isNotEmpty())

                    Glide.with(context)
                        .load(x.images.first())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.logo_transparent_png)
                        .into(imageView7)

                textView6.text =
                    if (x.reviews.size < 3)
                        "${x.reviews.size} users reviewed"
                    else
                        "reviews ${x.reviews.size}"

                println("servingg => ${x.offerings}")
                placeServings.adapter = ServingListAdapter(x.servings.toMutableList())
                root.setOnClickListener { onclick(x) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.placepreview, parent, false)
        return VH(PlacepreviewBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindItem(places[position])
    }
}