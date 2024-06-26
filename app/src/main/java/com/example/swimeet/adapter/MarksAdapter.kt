package com.example.swimeet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Mark
import com.example.swimeet.util.FirebaseUtil
import com.google.android.material.card.MaterialCardView

class MarksAdapter(private var marksList: List<Mark> = emptyList()) :
    RecyclerView.Adapter<MarksAdapter.MarksViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarksViewHolder {
        return MarksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.mark_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MarksViewHolder, position: Int) {
        holder.render(marksList[position])
    }

    override fun getItemCount(): Int = marksList.size

    fun updateList(newList: List<Mark>) {
        val diffResult = DiffUtil.calculateDiff(MarksDiffUtil(newList, marksList))
        marksList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class MarksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSwimEvent: TextView = itemView.findViewById(R.id.tvEventName)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvCompetition)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvMark: TextView = itemView.findViewById(R.id.tvMark)
        fun render(mark: Mark) {
            tvSwimEvent.text = mark.swimEvent
            tvLocation.text = mark.competition
            tvDate.text = FirebaseUtil.parseFirebaseTimestamp(mark.date!!)
            tvMark.text = FirebaseUtil.milisecondsToSwimTime(mark.mark)

            itemView.setOnLongClickListener { card ->
                (card as MaterialCardView).isChecked = !card.isChecked
                true
            }
        }

    }
}