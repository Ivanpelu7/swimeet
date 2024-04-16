package com.example.swimeet.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
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

        fun render(user: User) {
            tvNombre.text = user.username

            itemLayout.setOnClickListener {
                val intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra("otherUsername", user.username)
                intent.putExtra("otherUserId", user.userId)
                context.startActivity(intent)
            }
        }
    }
}

