package com.example.recycleviewwithclicklistener

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorWindow
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.reflect.Field


class SQLiteHelper(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION+1) {

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "mangrove.db"
        private const val TBL_MANGROVE ="tbl_mangrove"
        private const val ID = "id"
        private const val NAME = "name"
        private const val IMAGE = "image"
        private const val DATE = "date"


    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTblMangrove = ("CREATE TABLE "+ TBL_MANGROVE + "("
                + ID + " INTEGER PRIMARY KEY, " + NAME + " TEXT, " + DATE + " TEXT, " +  IMAGE + " BLOB" + ")")
        db?.execSQL(createTblMangrove)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_MANGROVE")
        onCreate(db)
    }

    fun insertMangrove(mg:MangroveModel):Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID,mg.id)
        contentValues.put(NAME,mg.name)
        contentValues.put(DATE,mg.date)
        contentValues.put(IMAGE,mg.image)

        val success = db.insert(TBL_MANGROVE, "id="+ mg.id , contentValues)
        db.close()
        return success
    }
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

        var id:Int
        var name:String
        var date:String
        var image: ByteArray


        if(cursor.moveToFirst()){
            do{
                id=cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                name=cursor.getString(cursor.getColumnIndexOrThrow("name"))
                date=cursor.getString(cursor.getColumnIndexOrThrow("date"))
                image=cursor.getBlob(cursor.getColumnIndexOrThrow("image"))

                val mg = MangroveModel(id=id, name=name, date=date, image=image)
                mgList.add(mg)

            }while (cursor.moveToNext())
        }
        return mgList
    }

    fun deleteMangroveId(id:Int):Int{
        val db = this.writableDatabase

        val contentValues=ContentValues()
        contentValues.put(ID,id)

        val success = db.delete(TBL_MANGROVE,"id=$id",null)
        db.close()
        return success
    }

}



