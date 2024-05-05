package com.example.swimeet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Event
import com.example.swimeet.util.FirebaseUtil

class EventsAdapter(private var eventsList: List<Event> = emptyList()) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        return EventsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        )
    }

    fun updateList(newList: List<Event>) {
        val diffResult = DiffUtil.calculateDiff(EventsDiffUtil(eventsList, newList))
        eventsList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = eventsList.size

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.render(eventsList[position])
    }

    class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.tvNameEvent)
        private val eventDate: TextView = itemView.findViewById(R.id.tvFechaVar)
        private val participants: TextView = itemView.findViewById(R.id.tvParticipantsVar)
        private val iconEvent: ImageView = itemView.findViewById(R.id.ivEventIcon)
        fun render(event: Event) {
            when (event.type) {
                "Comida" -> iconEvent.setImageResource(R.drawable.dinner)
                "Fiesta" -> iconEvent.setImageResource(R.drawable.party)
                "Quedada" -> iconEvent.setImageResource(R.drawable.group)
            }

            eventName.text = event.name
            eventDate.text = FirebaseUtil.timestampToStringDate(event.date!!)
            participants.text = event.participants.size.toString()
        }

    }
}