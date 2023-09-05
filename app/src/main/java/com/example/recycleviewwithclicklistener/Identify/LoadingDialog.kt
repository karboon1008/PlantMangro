package com.example.recycleviewwithclicklistener.Identify

import androidx.appcompat.app.AlertDialog
import com.example.recycleviewwithclicklistener.R

class LoadingDialog (val mresult_activity: result_activity){
    private lateinit var isdialog: AlertDialog
    fun startLoading(){
        //set View
        val inflater = mresult_activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.progressbar, null)
        //set Dialog
        val builder = AlertDialog.Builder(mresult_activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isdialog = builder.create()
        isdialog.show()
    }
    fun isDismiss(){
        isdialog.dismiss()
    }
}