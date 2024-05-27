package com.example.swimeet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.swimeet.R
import com.example.swimeet.data.model.Comment
import com.example.swimeet.util.FirebaseUtil

class CommentsAdapter(private var commentsList: List<Comment> = emptyList()) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsAdapter.CommentViewHolder {
        return CommentsAdapter.CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        )
    }

    fun updateList(newList: List<Comment>) {
        val diffResult = DiffUtil.calculateDiff(CommentsDiffUtil(commentsList, newList))
        commentsList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.CommentViewHolder, position: Int) {
        holder.render(commentsList[position])
    }

    override fun getItemCount(): Int = commentsList.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val iv: ImageView = itemView.findViewById(R.id.iv)

        fun render(comment: Comment) {
            tvUsername.text = comment.username
            tvMessage.text = comment.message



            FirebaseUtil.getUsersRef().document(comment.userId).get().addOnSuccessListener {
                val photo = it.get("photo")

                Glide.with(iv.context)
                    .load(photo)
                    .transform(CircleCrop())
                    .into(iv)
            }


        }
    }


}
