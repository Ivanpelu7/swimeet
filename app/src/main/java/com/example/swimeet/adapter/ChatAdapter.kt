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
import com.google.firebase.Timestamp
import org.ocpsoft.prettytime.PrettyTime
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

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
        private val prettyTime: PrettyTime = PrettyTime(Locale("es"))

        fun render(chat: Chat, user: User) {
            tvName.text = user.username
            Glide.with(tvName.context)
                .load(user.photo.toUri())
                .transform(CircleCrop())
                .into(ivImage)

            if (chat.lastMessage != "" && chat.lastMessageTimestamp != null) {

                if ((isBeforeMidnight(chat.lastMessageTimestamp!!)) && (hasPassedOneDay(chat.lastMessageTimestamp!!))) {
                    tvTimestamp.text = "Ayer"
                } else if (!hasPassedOneDay(chat.lastMessageTimestamp!!)) {
                    tvTimestamp.text = prettyTime.format(chat.lastMessageTimestamp!!.toDate())
                } else {
                    tvTimestamp.text = FirebaseUtil.timestampToString(chat.lastMessageTimestamp!!)
                }

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

        private fun hasPassedOneDay(timestamp: Timestamp): Boolean {
            // Convertir el timestamp de Firebase a Instant
            val timestampInstant = timestamp.toDate().toInstant()

            // Obtener el instante actual
            val nowInstant = Instant.now()

            // Calcular la diferencia en días entre el timestamp y ahora
            val daysBetween = ChronoUnit.DAYS.between(timestampInstant, nowInstant)

            // Comprobar si ha pasado más de un día
            return daysBetween < 2
        }

        private fun isBeforeMidnight(timestamp: Timestamp): Boolean {
            // Obtener el instante actual
            val nowInstant = Instant.now()

            // Convertir el timestamp de Firebase a un instante
            val timestampInstant = timestamp.toDate().toInstant()

            // Obtener la fecha actual en la zona horaria predeterminada
            val currentDate = LocalDate.now(ZoneId.systemDefault())

            // Establecer la hora a 00:00:00 para comparar solo las fechas
            val startOfDay = currentDate.atStartOfDay()
                .toInstant(ZoneId.systemDefault().rules.getOffset(nowInstant))

            // Comparar el timestamp con la fecha de inicio del día actual
            return timestampInstant.isBefore(startOfDay)
        }
    }
}
