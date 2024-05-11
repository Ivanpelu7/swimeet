package com.example.swimeet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.SwimEvent
import com.example.swimeet.ui.SwimEventDetailActivity

class SwimEventsAdapter(private var swimEventsList: List<SwimEvent> = emptyList()) :
    RecyclerView.Adapter<SwimEventsAdapter.SwimEventViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SwimEventsAdapter.SwimEventViewHolder {
        return SwimEventViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.swimevent_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SwimEventsAdapter.SwimEventViewHolder, position: Int) {
        holder.render(swimEventsList[position])
    }

    override fun getItemCount(): Int = swimEventsList.size

    class SwimEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.tvName)
        private val img: ImageView = itemView.findViewById(R.id.ivSwimEvent)
        fun render(swimEvent: SwimEvent) {
            name.text = swimEvent.name
            swimEvent.img?.let { img.setImageResource(it) }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, SwimEventDetailActivity::class.java)
                intent.putExtra("name", swimEvent.name)
                itemView.context.startActivity(intent)
            }
        }

    }
}