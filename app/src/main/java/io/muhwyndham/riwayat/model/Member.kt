package io.muhwyndham.riwayat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_table")
data class Member (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "phoneNumber") var phoneNumber: Int = 0,
    @ColumnInfo(name = "memberName") var memberName: String = ""
)