package com.example.swimeet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Mark

class AllMarksAdapter(private val marksList: List<Mark>) : RecyclerView.Adapter<AllMarksAdapter.MarksViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarksViewHolder {
        return MarksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.mark_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AllMarksAdapter.MarksViewHolder, position: Int) {
        holder.render(marksList[position])
    }

    override fun getItemCount(): Int = marksList.size

    class MarksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun render(mark: Mark) {

        }

    }
}