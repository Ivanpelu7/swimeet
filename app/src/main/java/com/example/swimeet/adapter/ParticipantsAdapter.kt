package com.example.swimeet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.swimeet.R
import com.example.swimeet.data.model.User
import com.example.swimeet.ui.ChatRoomActivity
import com.example.swimeet.util.FirebaseUtil

class ParticipantsAdapter(private var participantsList: List<User> = emptyList()) :
    RecyclerView.Adapter<ParticipantsAdapter.ParticipantsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParticipantsAdapter.ParticipantsViewHolder {
        return ParticipantsAdapter.ParticipantsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.participant_item, parent, false)
        )
    }

    fun updateList(newList: List<User>) {
        val diffResult = DiffUtil.calculateDiff(ParticipantsDiffUtil(participantsList, newList))
        participantsList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(
        holder: ParticipantsViewHolder,
        position: Int
    ) {
        holder.render(participantsList[position])
    }

    override fun getItemCount(): Int = participantsList.size

    class ParticipantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUser)
        private val avatar: ImageView = itemView.findViewById(R.id.ivAvatarUser)
        private val btn: Button = itemView.findViewById(R.id.btnStartChat)
        fun render(user: User) {
            Glide.with(avatar.context)
                .load(user.photo.toUri())
                .transform(CircleCrop())
                .into(avatar)

            tvUsername.text = user.username

            if (user.userId == FirebaseUtil.getCurrentUserID()) {
                btn.isEnabled = false
            }

            btn.setOnClickListener {
                val intent = Intent(itemView.context, ChatRoomActivity::class.java)
                intent.putExtra("otherUserId", user.userId)
                intent.putExtra("otherUsername", user.username)
                intent.putExtra("otherUserImage", user.photo)
                itemView.context.startActivity(intent)
            }
        }
    }
}