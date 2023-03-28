package com.example.recycleviewwithclicklistener

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import androidx.room.*

data class Collection (val image: Bitmap, val name:String, val commonName: String, val explanation:String, val short:String) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable<Bitmap>(Bitmap::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(image, flags)
        parcel.writeString(name)
        parcel.writeString(commonName)
        parcel.writeString(explanation)
        parcel.writeString(short)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Collection> {
        override fun createFromParcel(parcel: Parcel): Collection {
            return Collection(parcel)
        }

        override fun newArray(size: Int): Array<Collection?> {
            return arrayOfNulls(size)
        }
    }
    @Database(entities = [Collection::class], version = 1)
    abstract class CollectionDatabase : RoomDatabase() {
        abstract fun collectionDao(): CollectionDao
    }

    @Dao
    interface CollectionDao {
        @Insert
        fun insert(item: Collection)

        //@Query("SELECT * FROM Collection")
        fun getAll(): List<Collection>

        @Delete
        fun delete(item: Collection)
    }
}
