package com.example.swimeet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.data.model.Message
import com.example.swimeet.util.FirebaseUtil

class MessagesAdapter(private var messagesList: List<Message> = emptyList()) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    fun updateList(newList: List<Message>) {
        val diffResult = DiffUtil.calculateDiff(MessagesDiffUtil(messagesList, newList))
        messagesList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        )
    }

    override fun getItemCount(): Int = messagesList.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.render(messagesList[position])
    }


    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val receivedMessage: LinearLayout = itemView.findViewById(R.id.messageReceived)
        private val sendedMessage: LinearLayout = itemView.findViewById(R.id.messageSended)
        private val tvMessageSended: TextView = itemView.findViewById(R.id.tvMessageSended)
        private val tvReceivedMessage: TextView = itemView.findViewById(R.id.tvMessageReceived)
        fun render(message: Message) {
            if (message.idSender == FirebaseUtil.getCurrentUserID()) {
                receivedMessage.visibility = View.GONE
                sendedMessage.visibility = View.VISIBLE
                tvMessageSended.text = message.message

            } else {
                receivedMessage.visibility = View.VISIBLE
                sendedMessage.visibility = View.GONE
                tvReceivedMessage.text = message.message
            }
        }


    }
}