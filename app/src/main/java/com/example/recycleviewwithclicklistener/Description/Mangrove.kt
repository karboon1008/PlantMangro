package com.example.recycleviewwithclicklistener.Description

import android.os.Parcel
import android.os.Parcelable

data class Mangrove (val image: Int, val name:String, val commonName: String, val explanation:String, val short:String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(image)
        parcel.writeString(name)
        parcel.writeString(commonName)
        parcel.writeString(explanation)
        parcel.writeString(short)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mangrove> {
        override fun createFromParcel(parcel: Parcel): Mangrove {
            return Mangrove(parcel)
        }

        override fun newArray(size: Int): Array<Mangrove?> {
            return arrayOfNulls(size)
        }
    }
}