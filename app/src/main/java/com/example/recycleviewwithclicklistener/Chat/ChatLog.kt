package com.example.recycleviewwithclicklistener.Chat

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recycleviewwithclicklistener.R
import com.example.recycleviewwithclicklistener.SignUp.Users
import com.example.recycleviewwithclicklistener.databinding.ActivityChatLogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class ChatLog : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }

    private lateinit var binding: ActivityChatLogBinding
    private val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: Users? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewChatLog.adapter = adapter

        // display user name
        toUser = intent.getParcelableExtra<Users>(LatestMessages.USER_KEY)

        supportActionBar?.title = toUser?.name

        //setDummyData()
        listenForMessages()

        binding.btnChatLog.setOnClickListener {
            Log.d("ChatLog", "Attempt to send message")
            performSentMessage()
        }
    }

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = Chat.currentUser ?: return
                        adapter.add(ChatSentItem(chatMessage.text, currentUser, chatMessage.timestamp))
                    } else {
                        adapter.add(ChatReceiveItem(chatMessage.text,toUser!!, chatMessage.timestamp))
                    }
                }
                binding.recyclerViewChatLog.scrollToPosition(adapter.itemCount -1 )
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    // send message to firebase
    private fun performSentMessage() {
        // message
        val text = binding.editTxtChatLog.text.toString()
        // sender Id
        val fromId = FirebaseAuth.getInstance().uid
        // receiver Id
        val user = intent.getParcelableExtra<Users>(LatestMessages.USER_KEY)
        if (user == null) return
        val toId = user.uid

        if (fromId == null || toId == null) return

        // for sent message
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        // for recieve message
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${ref.key}")
                binding.editTxtChatLog.text.clear()
                binding.recyclerViewChatLog.scrollToPosition(adapter.itemCount - 1)
            }

        toRef.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestToMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestToMessageRef.setValue(chatMessage)
    }

}

class ChatSentItem(val text: String, val user: Users, val timestamp: Long): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // load message into textview
        viewHolder.itemView.findViewById<TextView>(R.id.txt_sent_message).text = text

        // load user image into imageview
        val uri = user.profileImageUri
        val targetImageView =
            viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView_chatSent)
        Picasso.get().load(uri).into(targetImageView)

        // load timestamp into textview
        val hours = (timestamp / 3600).toInt() % 12  // 12-hour format
        val minutes = (timestamp % 3600 / 60).toInt()
        val amPm = if (hours >= 12) "PM" else "AM"

        val formattedTime = String.format("%02d:%02d %s", if (hours % 12 == 0) 12 else hours % 12, minutes, amPm) // Combine hours and minutes into HHMM format
        Log.d("Chatlog", "Current time: $formattedTime")
        viewHolder.itemView.findViewById<TextView>(R.id.text_timestamp_sent).text = formattedTime

    }

    override fun getLayout(): Int {
        return R.layout.sent
    }
}

class ChatReceiveItem(val text: String, val user: Users, val timestamp: Long): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // load message into textview
        viewHolder.itemView.findViewById<TextView>(R.id.txt_receive_message).text = text

        // load user image into imageview
        val uri = user.profileImageUri
        val targetImageView = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView_chatReceive)
        Picasso.get().load(uri).into(targetImageView)

        // load timestamp into textview
        val hours = (timestamp / 3600).toInt() % 12  // 12-hour format
        val minutes = (timestamp % 3600 / 60).toInt()
        val amPm = if (hours >= 12) "PM" else "AM"
        val formattedTime = String.format("%02d:%02d %s", if (hours % 12 == 0) 12 else hours % 12, minutes, amPm) // Combine hours and minutes into HHMM format
        Log.d("Chatlog", "Current time: $formattedTime")
        viewHolder.itemView.findViewById<TextView>(R.id.text_timestamp_receive).text = formattedTime

    }

    override fun getLayout(): Int {
        return R.layout.receive
    }

}
