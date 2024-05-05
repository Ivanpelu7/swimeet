package com.example.swimeet.adapter

import android.content.Context
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
import com.example.swimeet.data.model.User
import com.example.swimeet.ui.ChatRoomActivity

class UsersAdapter(private var userList: List<User> = emptyList()) :
    RecyclerView.Adapter<UsersAdapter.UsuarioViewHolder>() {

    fun updateList(newList: List<User>) {
        val diffResult = DiffUtil.calculateDiff(UsersDiffUtil(userList, newList))
        userList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UsuarioViewHolder(layoutInflater.inflate(R.layout.user_item, parent, false))
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val item = userList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = userList.size

    class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvNombre: TextView = view.findViewById(R.id.tvUser)
        private val itemLayout: CardView = view.findViewById(R.id.itemLayout)
        private val context: Context = view.context
        private val ivUserAvatar: ImageView = view.findViewById(R.id.ivAvatarUser)

        fun render(user: User) {
            tvNombre.text = user.username

            Glide.with(ivUserAvatar.context)
                .load(user.photo.toUri())
                .transform(CircleCrop())
                .into(ivUserAvatar)

            itemLayout.setOnClickListener {
                val intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra("otherUsername", user.username)
                intent.putExtra("otherUserId", user.userId)
                intent.putExtra("otherUserImage", user.photo)
                context.startActivity(intent)
            }
        }
    }
}

