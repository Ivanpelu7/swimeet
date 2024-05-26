package com.example.swimeet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Event
import com.example.swimeet.ui.CompetitionDetailActivity
import com.example.swimeet.util.FirebaseUtil

class EventsAdapter(private var eventsList: List<Event> = emptyList()) :
    RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

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
        private val eventPlace: TextView = itemView.findViewById(R.id.tvSitioVar)
        fun render(event: Event) {
            when (event.type) {
                "Comida" -> iconEvent.setImageResource(R.drawable.dinner)
                "Fiesta" -> iconEvent.setImageResource(R.drawable.party)
                "Quedada" -> iconEvent.setImageResource(R.drawable.meeting)
            }

            eventName.text = event.name
            eventDate.text = FirebaseUtil.timestampToStringDate(event.date!!)
            participants.text = event.participants.size.toString()
            eventPlace.text = event.place

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, CompetitionDetailActivity::class.java)
                intent.putExtra("id", event.eventId)
                intent.putExtra("name", event.name)
                intent.putExtra("latitude", event.location!!.latitude.toString())
                intent.putExtra("longitude", event.location!!.longitude.toString())
                intent.putExtra("type", "1")
                itemView.context.startActivity(intent)
            }
        }

    }
}