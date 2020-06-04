package io.muhwyndham.riwayat.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.muhwyndham.riwayat.model.Member

@Dao
interface MemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setMember(member: Member)

    @Query("SELECT * FROM member_table ORDER BY memberName ASC")
    fun getAllMember(): LiveData<MutableList<Member>>

    @Query("SELECT * FROM member_table WHERE phoneNumber = :phoneNumber")
    fun getMember(phoneNumber: String): LiveData<Member>

    @Query("DELETE FROM member_table")
    fun deleteAll()

    @Delete
    fun delete(member: Member)

}