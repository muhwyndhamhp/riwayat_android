package io.muhwyndhamhp.riwayat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_table")
data class Member (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "phoneNumber") var phoneNumber: String = "",
    @ColumnInfo(name = "memberName") var memberName: String = "",
    @ColumnInfo(name = "uuid") var uuid: String = "",
    @ColumnInfo(name = "isAdmin") var isAdmin : Boolean = false
)