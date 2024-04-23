package com.example.swimeet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Competition

class CompetitionsAdapter(private var competitionsList: List<Competition> = emptyList()) : RecyclerView.Adapter<CompetitionsAdapter.CompetitionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionsViewHolder {
        return CompetitionsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.competition_item, parent, false)
        )
    }

    override fun getItemCount(): Int = competitionsList.size

    override fun onBindViewHolder(holder: CompetitionsViewHolder, position: Int) {
        holder.render(competitionsList[position])
    }

    class CompetitionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.tvNameCompetition)
        private val eventSite: TextView = itemView.findViewById(R.id.tvSitioVar)
        private val eventDate: TextView = itemView.findViewById(R.id.tvFechaVar)
        private val eventDistanceVar: TextView = itemView.findViewById(R.id.tvDistance)
        private val participants: TextView = itemView.findViewById(R.id.tvParticipantsVar)
        private val iconEvent: ImageView = itemView.findViewById(R.id.ivCompetitionIcon)
        fun render(competition: Competition) {
            if (competition.type == "Travesía") {
                iconEvent.setImageResource(R.drawable.sea)
            } else {
                iconEvent.setImageResource(R.drawable.pool)
            }

            if (competition.distance != null) {
                var distanceInt = ""

                if (competition.distance!! % 1 == 0.0) {
                    distanceInt = competition.distance!!.toInt().toString() + "KM"
                } else {
                    distanceInt = competition.distance!!.toString() + "KM"
                }

                eventDistanceVar.visibility = View.VISIBLE
                eventDistanceVar.text = distanceInt.replace(".", ",")

            } else {
                eventDistanceVar.visibility = View.GONE
            }

            eventName.text = competition.name
            eventSite.text = competition.site
            eventDate.text = competition.date.toString()
            participants.text = competition.participants.size.toString()
        }

    }
}