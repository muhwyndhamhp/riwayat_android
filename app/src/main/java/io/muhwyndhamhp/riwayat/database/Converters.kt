package io.muhwyndhamhp.riwayat.database


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    companion object{
        @TypeConverter
        @JvmStatic
        fun fromString(strings: String): List<String> {
            val listType =
                object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(strings, listType)
        }
        @TypeConverter
        @JvmStatic
        fun fromList(strings: List<String>): String {
            val gson = Gson()
            return gson.toJson(strings)
        }
    }

}