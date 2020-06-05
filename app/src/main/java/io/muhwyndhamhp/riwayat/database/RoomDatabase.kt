package io.muhwyndhamhp.riwayat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.muhwyndhamhp.riwayat.dao.MemberDao
import io.muhwyndhamhp.riwayat.model.Member

@Database(entities = [Member::class], version = 1, exportSchema = false)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao

    companion object {

        @Volatile
        private var INSTANCE: io.muhwyndhamhp.riwayat.database.RoomDatabase? = null

        fun getDatabase(context: Context): io.muhwyndhamhp.riwayat.database.RoomDatabase? {
            synchronized(RoomDatabase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        io.muhwyndhamhp.riwayat.database.RoomDatabase::class.java, "room_database"
                    )
                        .build()
                }
            }
            return INSTANCE
        }
    }
}