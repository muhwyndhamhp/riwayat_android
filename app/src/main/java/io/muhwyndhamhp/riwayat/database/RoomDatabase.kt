package io.muhwyndhamhp.riwayat.database

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase
import io.muhwyndhamhp.riwayat.dao.CaseDao
import io.muhwyndhamhp.riwayat.dao.CaseNoteDao
import io.muhwyndhamhp.riwayat.dao.MemberDao
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.CaseNote
import io.muhwyndhamhp.riwayat.model.Member

@Database(entities = [Member::class, Case::class, CaseNote::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun caseDao(): CaseDao
    abstract fun caseNoteDao() : CaseNoteDao

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