package com.example.swimeet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Advertisement

class ViewPagerAdapter(private var advertisementsList: List<Advertisement> = emptyList()) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.advertisement_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    fun updateList(newList: List<Advertisement>) {
        advertisementsList = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.render(advertisementsList[position])
    }

    override fun getItemCount(): Int = advertisementsList.size

    class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)

        fun render(advertisement: Advertisement) {
            tvTitle.text = advertisement.title
            tvMessage.text = advertisement.message
            tvAuthor.text = advertisement.authorUsername
        }
    }
}