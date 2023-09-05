package com.example.recycleviewwithclicklistener.Guideline

import android.os.Parcel
import android.os.Parcelable


data class GuidelineModel (val activityName:String, val title:String, val desc: String, val image1: Int, val image2: Int, val image3:Int?, val image4:Int?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()


    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(activityName)
        parcel.writeString(title)
        parcel.writeString(desc)
        parcel.writeInt(image1)
        parcel.writeInt(image2)
        if (image3 != null) {
            parcel.writeInt(image3)
        }
        if (image4 != null) {
            parcel.writeInt(image4)
        }

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GuidelineModel> {
        override fun createFromParcel(parcel: Parcel): GuidelineModel {
            return GuidelineModel(parcel)
        }

        override fun newArray(size: Int): Array<GuidelineModel?> {
            return arrayOfNulls(size)
        }
    }
}