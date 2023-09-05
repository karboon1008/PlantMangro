package com.example.recycleviewwithclicklistener.Chat

import android.util.Log
import android.widget.TextView
import com.example.recycleviewwithclicklistener.R
import com.example.recycleviewwithclicklistener.SignUp.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class UserItems(val user: Users) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_layout
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // call list for each user object
        viewHolder.itemView.findViewById<TextView>(R.id.txt_name).text = user.name
        // Load profile image from firebase
        Picasso.get().load(user.profileImageUri).resize(800,0).centerCrop().into(viewHolder.itemView.findViewById<CircleImageView>(R.id.selectProfile))
        // Display the latest message
        //viewHolder.itemView.findViewById<TextView>(R.id.txt_latestMessage).text = latestMessage.text
        Log.d("UserItem", "user profile url: ${user.profileImageUri}")
    }
}