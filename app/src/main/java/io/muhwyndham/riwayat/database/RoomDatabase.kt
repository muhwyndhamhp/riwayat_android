package io.muhwyndham.riwayat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.muhwyndham.riwayat.dao.MemberDao
import io.muhwyndham.riwayat.model.Member

@Database(entities = [Member::class], version = 1, exportSchema = false)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao

    companion object {

        @Volatile
        private var INSTANCE: io.muhwyndham.riwayat.database.RoomDatabase? = null

        fun getDatabase(context: Context): io.muhwyndham.riwayat.database.RoomDatabase? {
            synchronized(RoomDatabase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        io.muhwyndham.riwayat.database.RoomDatabase::class.java, "room_database"
                    )
                        .build()
                }
            }
            return INSTANCE
        }
    }
}