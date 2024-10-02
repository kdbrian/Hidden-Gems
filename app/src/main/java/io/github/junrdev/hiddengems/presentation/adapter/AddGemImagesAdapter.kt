package io.github.junrdev.hiddengems.presentation.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.github.junrdev.hiddengems.R

class AddGemImagesAdapter(
    val images: MutableList<Uri>
) : RecyclerView.Adapter<AddGemImagesAdapter.VH>() {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(x: Uri) {
            view.apply {
                findViewById<ImageView>(R.id.imageView8).setImageURI(x)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddGemImagesAdapter.VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bannerimage, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: AddGemImagesAdapter.VH, position: Int) {
        val image = images[position]
        holder.bindItem(image)
    }

    override fun getItemCount(): Int {
        return images.size
    }


}