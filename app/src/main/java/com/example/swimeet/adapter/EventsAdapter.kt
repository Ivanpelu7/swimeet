package com.example.swimeet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Event

class EventsAdapter(private var eventsList: List<Event> = emptyList()) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        return EventsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        )
    }

    override fun getItemCount(): Int = eventsList.size

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.render(eventsList[position])
    }

    class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.tvNameEvent)
        private val eventSite: TextView = itemView.findViewById(R.id.tvSitioVar)
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
            eventSite.text = event.site
            eventDate.text = event.date.toString()
            participants.text = event.participants.size.toString()
        }

    }
}