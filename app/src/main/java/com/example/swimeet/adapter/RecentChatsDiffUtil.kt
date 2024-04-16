package com.example.swimeet.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.swimeet.data.model.Chat

class RecentChatsDiffUtil(
    private val oldList: List<Chat>,
    private val newList: List<Chat>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].chatId == newList[newItemPosition].chatId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]

    }
}