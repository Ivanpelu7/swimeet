package com.example.swimeet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Mark
import com.example.swimeet.util.FirebaseUtil

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

    override fun onBindViewHolder(holder: MarksAdapter.MarksViewHolder, position: Int) {
        holder.render(marksList[position])
    }

    override fun getItemCount(): Int = marksList.size
    fun updateList(newList: List<Mark>) {
        marksList = newList
        notifyDataSetChanged()
    }

    class MarksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSwimEvent: TextView = itemView.findViewById(R.id.tvEventName)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvMark: TextView = itemView.findViewById(R.id.tvMark)
        fun render(mark: Mark) {
            tvSwimEvent.text = mark.swimEvent
            tvLocation.text = mark.competition
            tvDate.text = FirebaseUtil.parseFirebaseTimestamp(mark.date!!)
            tvMark.text = FirebaseUtil.milisecondsToSwimTime(mark.mark)
        }

    }
}