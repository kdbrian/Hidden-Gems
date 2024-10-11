package io.github.junrdev.hiddengems.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.Review
import io.github.junrdev.hiddengems.databinding.ReviewitemBinding

class ReviewListAdapter(
    val reviews: List<Review>
) : RecyclerView.Adapter<ReviewListAdapter.VH>() {
    inner class VH(private val binding: ReviewitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(x: Review) {
            binding.review = x
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reviewitem, parent, false)
        return VH(ReviewitemBinding.bind(view))
    }

    override fun getItemCount(): Int = reviews.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindItem(reviews[position])
    }

}