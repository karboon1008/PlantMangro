package com.example.recycleviewwithclicklistener.SignUp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var uid: String? = null,
    var profileImageUri: String? = null
):Parcelable{
    constructor() : this("","","","","")
}
