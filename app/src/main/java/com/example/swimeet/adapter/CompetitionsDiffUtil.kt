package com.example.swimeet.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.swimeet.data.model.Competition

class CompetitionsDiffUtil(
    private val oldList: List<Competition>,
    private val newList: List<Competition>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].eventId == newList[newItemPosition].eventId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}