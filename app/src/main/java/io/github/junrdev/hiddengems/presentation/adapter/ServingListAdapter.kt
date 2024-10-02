package io.github.junrdev.hiddengems.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.data.model.Serving

class ServingListAdapter(
    val servings: MutableList<Serving>
) : RecyclerView.Adapter<ServingListAdapter.VH>() {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(x: Serving) {
            view.apply {
                findViewById<TextView>(R.id.textView7).text = x.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.featureitem, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int {
        return servings.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindItem(servings[position])
    }


}