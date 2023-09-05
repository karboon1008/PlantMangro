package com.example.recycleviewwithclicklistener.Collection

import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleviewwithclicklistener.R

class CollectionAdapter(private val sqLiteHelper: SQLiteHelper) :RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>(){

    private var mgList:ArrayList<MangroveModel> = ArrayList()
    private var onClickDeleteItem: ((MangroveModel) -> Unit)? = null
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(mg: MangroveModel)
    }

    fun addItems(items:ArrayList<MangroveModel>){
        this.mgList = items
        notifyDataSetChanged()
    }

    fun setOnClickDeleteItem(callback:(MangroveModel)->Unit){
        this.onClickDeleteItem = callback
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // Function to check internet connectivity
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected ?: false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CollectionViewHolder((
            LayoutInflater.from(parent.context).inflate(R.layout.card_items_collection,parent,false))
    )

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val mg = mgList[position]
        holder.bindView(mg)
        holder.itemView.setOnClickListener { listener?.onItemClick(mg) }
        holder.btnDelete.setOnClickListener{onClickDeleteItem?.invoke(mg)}
        holder.uploadBtn.setOnClickListener {
            val builder = AlertDialog.Builder(holder.view.context)
            builder.setMessage("Are you sure you want to upload this item?")
            builder.setCancelable(true)
            builder.setPositiveButton("Yes") { dialog, _ ->
                // call the uploadMangroveByDate function and store the data
                val uploadSuccess = sqLiteHelper.uploadMangroveByDate(mg.date)

                if(!isInternetAvailable(holder.view.context) && uploadSuccess){
                    Toast.makeText(holder.view.context, "Will be uploaded automatically once there is internet connection", Toast.LENGTH_SHORT).show()
                }
                else if (isInternetAvailable((holder.view.context)) && uploadSuccess) {
                    Toast.makeText(holder.view.context, "Success uploaded", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(holder.view.context, "Upload Failed, try again", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun getItemCount(): Int {
        return mgList.size
    }

    inner class CollectionViewHolder(var view:View):RecyclerView.ViewHolder(view) {

        private var name = view.findViewById<TextView>(R.id.tvName)
        private var date = view.findViewById<TextView>(R.id.date)
        private var location = view.findViewById<TextView>(R.id.location)
        private var image = view.findViewById<ImageView>(R.id.saved_image)
        var btnDelete = view.findViewById<Button>(R.id.btn_delete)
        var uploadBtn = view.findViewById<Button>(R.id.btn_upload)

        fun bindView(mg: MangroveModel) {
            name.text = mg.name
            date.text = mg.date
            val latlng = (mg.latitude).toString() + ", " + (mg.longitude).toString()
            location.text = latlng


            //val bitmap = convertByteArrayToBitmap(mg.image)
            val imageBitmap = BitmapFactory.decodeByteArray(mg.image, 0, mg.image.size)
            image.setImageBitmap(imageBitmap)

        }

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val mg = mgList[position]
                    listener?.onItemClick(mg)
                }
            }
        }

    }

}