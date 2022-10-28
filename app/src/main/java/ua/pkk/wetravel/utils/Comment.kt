package ua.pkk.wetravel.retrofit

import androidx.recyclerview.widget.DiffUtil

data class Comment(
        val content: String,
        val time: String,
        val uid: String,
        val uimg: String,
        val uname: String
)

object COMMENT_DIFF_CALLBACK : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.equals(newItem)
    }

}