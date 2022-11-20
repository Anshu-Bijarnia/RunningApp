package com.example.runningapp.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import kotlin.jvm.internal.Ref.ByteRef

class Converters {
    @TypeConverter
    fun toBitmap (bytes: ByteArray) :Bitmap{
        return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
    }
    // Because complex types like bitmap couldn't be saved in the room database, bitmap is converted to byte array which is just binary code
    // that can be saved in the room database and then can be converted back to bitmap when needed by using the above function.
    @TypeConverter
    fun fromBitmap(bmp : Bitmap) : ByteArray{
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return outputStream.toByteArray()
    }
}