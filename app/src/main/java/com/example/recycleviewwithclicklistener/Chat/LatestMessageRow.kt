package com.example.recycleviewwithclicklistener.Chat

import android.widget.TextView
import com.example.recycleviewwithclicklistener.R
import com.example.recycleviewwithclicklistener.SignUp.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class LatestMessageRow(val latestMessage: ChatMessage): Item<GroupieViewHolder>() {
    // capture the info of user
    var chatPartnerUser : Users? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.txt_latestMessage).text = latestMessage.text

        // fetch user id
        val chatPartnerId : String
        if (latestMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = latestMessage.toId
        } else{
            chatPartnerId = latestMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(Users::class.java)
                viewHolder.itemView.findViewById<TextView>(R.id.txt_name).text = chatPartnerUser?.name

                val targetImageView = viewHolder.itemView.findViewById<CircleImageView>(R.id.selectProfile)
                Picasso.get().load(chatPartnerUser?.profileImageUri).into(targetImageView)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun getLayout(): Int {
        return R.layout.user_layout
    }

}