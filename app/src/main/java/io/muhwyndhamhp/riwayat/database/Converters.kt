package io.muhwyndhamhp.riwayat.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    companion object {
        /**
         * TODO later please change from GSON to manual implementation. because GSON is freaking Expensive!
         */
        @TypeConverter
        fun fromString(value: String?): ArrayList<String?>? {
            val listType =
                object : TypeToken<List<String?>?>() {}.type
            return Gson().fromJson(value, listType)
        }
        @TypeConverter
        fun fromList(list: List<String?>?): String? {
            val gson = Gson()
            return gson.toJson(list)
        }
    }
}