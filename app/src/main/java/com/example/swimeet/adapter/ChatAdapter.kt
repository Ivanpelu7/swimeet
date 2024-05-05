package com.example.swimeet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.swimeet.R
import com.example.swimeet.data.model.Chat
import com.example.swimeet.data.model.User
import com.example.swimeet.data.repository.UserRepository
import com.example.swimeet.ui.ChatRoomActivity
import com.example.swimeet.util.FirebaseUtil

class ChatAdapter(private var chatList: List<Chat> = emptyList()) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    fun updateList(newList: List<Chat>) {
        val diffResult = DiffUtil.calculateDiff(RecentChatsDiffUtil(chatList, newList))
        chatList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        )
    }

    override fun getItemCount(): Int = chatList.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val userRepository = UserRepository()
        userRepository.getOtherUser(chatList[position].usersId!!) { user ->
            holder.render(chatList[position], user)
        }
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tvUser)
        private val itemLayout: CardView = itemView.findViewById(R.id.itemLayout)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        private val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        private val tvNotification: TextView = itemView.findViewById(R.id.notificacion)
        private val ivImage: ImageView = itemView.findViewById(R.id.ivAvatar)

        fun render(chat: Chat, user: User) {
            tvName.text = user.username
            Glide.with(tvName.context)
                .load(user.photo.toUri())
                .transform(CircleCrop())
                .into(ivImage)

            if (chat.lastMessage != "" && chat.lastMessageTimestamp != null) {
                tvTimestamp.text =
                    FirebaseUtil.timestampToString(chat.lastMessageTimestamp!!)

                if (chat.lastMessage.length > 20) {
                    val shortString =
                        chat.lastMessage.substring(0, minOf(chat.lastMessage.length, 20))

                    tvLastMessage.text =
                        if (FirebaseUtil.getCurrentUserID() == chat.lastMessageSenderId) {
                            "Tu: ${shortString}..."
                        } else {
                            "${shortString}..."
                        }

                } else {
                    tvLastMessage.text =
                        if (FirebaseUtil.getCurrentUserID() == chat.lastMessageSenderId) {
                            "Tu: ${chat.lastMessage}"
                        } else {
                            chat.lastMessage
                        }
                }

            } else {
                tvLastMessage.text = null
                tvTimestamp.text = null
            }

            if (chat.unreadMessages > 0 && chat.lastMessageSenderId != FirebaseUtil.getCurrentUserID()) {
                tvNotification.apply {
                    visibility = View.VISIBLE
                    text = chat.unreadMessages.toString()
                }

            } else {
                tvNotification.visibility = View.GONE
            }

            itemLayout.setOnClickListener {
                val intent = Intent(itemView.context, ChatRoomActivity::class.java)
                intent.putExtra("otherUsername", user.username)
                intent.putExtra("otherUserId", user.userId)
                intent.putExtra("otherUserImage", user.photo)
                itemView.context.startActivity(intent)
            }
        }
    }
}
