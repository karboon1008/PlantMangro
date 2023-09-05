package com.example.recycleviewwithclicklistener.Collection

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorWindow
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.recycleviewwithclicklistener.Identify.DataRealtime
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.reflect.Field


class SQLiteHelper(context: Context):SQLiteOpenHelper(context,
    DATABASE_NAME,null,
    DATABASE_VERSION +1) {

    private lateinit var rdb: DatabaseReference

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "mangrove.db"
        private const val TBL_MANGROVE ="tbl_mangrove"
        private const val NAME = "name"
        private const val IMAGE = "image"
        private const val DATE = "date"
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"
    }

    // create table
    override fun onCreate(db: SQLiteDatabase?) {
        val createTblMangrove = ("CREATE TABLE "+ TBL_MANGROVE + "("
                + DATE + " DATE PRIMARY KEY , " + NAME + " TEXT, " + LATITUDE + " TEXT, "+  LONGITUDE + " TEXT, " + IMAGE + " BLOB" + ")")
        db?.execSQL(createTblMangrove)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_MANGROVE")
        onCreate(db)
    }

    // insert data into sqlite (local) database
    fun insertMangrove(mg: MangroveModel):Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(DATE,mg.date)
        contentValues.put(NAME,mg.name)
        contentValues.put(LATITUDE, mg.latitude)
        contentValues.put(LONGITUDE, mg.longitude)
        contentValues.put(IMAGE,mg.image)

        val success = db.insert(TBL_MANGROVE, null, contentValues)
        db.close()
        return success
    }

    // retrieve all the datab from sqlite database
    fun getAllMangrove(): ArrayList<MangroveModel>{
        val mgList:ArrayList<MangroveModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_MANGROVE"
        val db = this.readableDatabase

        val cursor: Cursor?

        try{
            cursor = db.rawQuery(selectQuery,null)
            try {
                val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
                field.isAccessible = true
                field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } catch (e:Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var name:String
        var date:String
        var latitude:String
        var longitude:String
        var image:ByteArray

        if(cursor.moveToFirst()){
            do{
                date=cursor.getString(cursor.getColumnIndexOrThrow("date"))
                name=cursor.getString(cursor.getColumnIndexOrThrow("name"))
                latitude=cursor.getString(cursor.getColumnIndexOrThrow("latitude"))
                longitude=cursor.getString(cursor.getColumnIndexOrThrow("longitude"))
                image=cursor.getBlob(cursor.getColumnIndexOrThrow("image"))

                val mg = MangroveModel(date=date, name=name, latitude = latitude, longitude = longitude, image=image)
                mgList.add(mg)

            }while (cursor.moveToNext())
        }
        return mgList
    }

    // delete one row data from sqlite database
    fun deleteMangroveDate(date: String): Int {
        val db = this.writableDatabase
        // Delete the row with the specified date
        val success = db.delete(TBL_MANGROVE, "DATE=?", arrayOf(date))
        db.close()
        Log.d("SQLiteHelper", "Deleted $success rows with date: $date")
        return success
    }

    // upload one row data to realtime database
    fun uploadMangroveByDate(date: String): Boolean {
        val db = this.readableDatabase
        rdb = FirebaseDatabase.getInstance().getReference("items")

        val selectQuery = "SELECT * FROM $TBL_MANGROVE WHERE $DATE = ?"
        val selectionArgs = arrayOf(date)

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        if (cursor != null && cursor.moveToFirst()) {
            val location = LatLng(
                cursor.getString(cursor.getColumnIndexOrThrow("latitude")).toDouble(),
                cursor.getString(cursor.getColumnIndexOrThrow("longitude")).toDouble()
            ).toString()
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val image = cursor.getBlob(cursor.getColumnIndexOrThrow("image")).toString()
            val item = DataRealtime(name, date, location, image)

            // Get the current user's UID
            val userUid = FirebaseAuth.getInstance().currentUser?.uid

            // store under items, under user, under user ID with a unique ID for each item
            if (userUid!=null) {
                // Generate a unique key
                val itemRef = rdb.child("users").child(userUid).push()
                itemRef.setValue(item)
                cursor.close()
                return true
            }
            cursor.close()
        }
        return false
    }

    // retrieve location and image of data (for details when click marker in google map)
    fun getLocationMangrove(): Triple<ArrayList<String>,ArrayList<LatLng>,ArrayList<ByteArray>>{
        val mgLocationList:ArrayList<LatLng> = ArrayList()
        val namelist:ArrayList<String> = ArrayList()
        val imagelist:ArrayList<ByteArray> = ArrayList()

        val selectQuery = "SELECT NAME,LATITUDE, LONGITUDE, IMAGE FROM $TBL_MANGROVE"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        if(cursor!= null){
            while(cursor.moveToNext()) {
                val location = LatLng(cursor.getString(cursor.getColumnIndexOrThrow("latitude")).toDouble(), cursor.getString(cursor.getColumnIndexOrThrow("longitude")).toDouble())
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"))
                mgLocationList.add(location)
                namelist.add(name)
                imagelist.add(image)
            }
            cursor.close()
        }
        return Triple(namelist,mgLocationList,imagelist)
    }
}



