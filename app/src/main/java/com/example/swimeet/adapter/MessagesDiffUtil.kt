package com.example.swimeet.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.swimeet.data.model.Message

class MessagesDiffUtil(
    private val oldList: List<Message>,
    private val newList: List<Message>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].timestamp == newList[newItemPosition].timestamp
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]

    }
}