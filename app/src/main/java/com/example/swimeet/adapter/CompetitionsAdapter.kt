package com.example.swimeet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Message
import com.example.swimeet.ui.CompetitionDetailActivity
import com.example.swimeet.ui.MainActivity
import com.example.swimeet.util.FirebaseUtil

class CompetitionsAdapter(private var competitionsList: List<Competition> = emptyList()) : RecyclerView.Adapter<CompetitionsAdapter.CompetitionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionsViewHolder {
        return CompetitionsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.competition_item, parent, false)
        )
    }

    fun updateList(newList: List<Competition>) {
        val diffResult = DiffUtil.calculateDiff(CompetitionsDiffUtil(competitionsList, newList))
        competitionsList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = competitionsList.size

    override fun onBindViewHolder(holder: CompetitionsViewHolder, position: Int) {
        holder.render(competitionsList[position])
    }

    class CompetitionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.tvNameCompetition)
        private val eventDate: TextView = itemView.findViewById(R.id.tvFechaVar)
        private val eventDistanceVar: TextView = itemView.findViewById(R.id.tvDistance)
        private val eventPlace: TextView = itemView.findViewById(R.id.tvSitioVar)
        private val participants: TextView = itemView.findViewById(R.id.tvParticipantsVar)
        private val iconEvent: ImageView = itemView.findViewById(R.id.ivCompetitionIcon)
        fun render(competition: Competition) {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, CompetitionDetailActivity::class.java)
                intent.putExtra("id", competition.eventId)
                intent.putExtra("name", competition.name)
                intent.putExtra("latitude", competition.location!!.latitude.toString())
                intent.putExtra("longitude", competition.location!!.longitude.toString())
                itemView.context.startActivity(intent)
            }

            if (competition.type == "Travesía") {
                iconEvent.setImageResource(R.drawable.sea)
            } else {
                iconEvent.setImageResource(R.drawable.pool)
            }

            if (competition.distance != null) {
                val distanceFormated = String.format("%.1f", (competition.distance!! / 1000).toFloat())
                eventDistanceVar.text = "${distanceFormated}KM"

            } else {
                eventDistanceVar.visibility = View.GONE
            }

            eventName.text = competition.name
            eventDate.text = FirebaseUtil.timestampToStringDate(competition.date!!)
            participants.text = competition.participants.size.toString()
            eventPlace.text = competition.place

        }

    }
}