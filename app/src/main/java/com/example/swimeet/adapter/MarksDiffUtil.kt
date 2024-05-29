package com.example.swimeet.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.swimeet.data.model.Mark

class MarksDiffUtil(val newList: List<Mark>, private val oldList: List<Mark>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].date == oldList[oldItemPosition].date
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] == oldList[oldItemPosition]
    }
}