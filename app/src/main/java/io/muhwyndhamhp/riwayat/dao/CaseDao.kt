package io.muhwyndhamhp.riwayat.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.muhwyndhamhp.riwayat.model.Case

@Dao
interface CaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCase(case: Case)

    @Query("SELECT * FROM case_table ORDER BY nomor_lp")
    fun getAllCase(): LiveData<MutableList<Case>>

    @Query("SELECT * FROM case_table WHERE nomor_lp = :nomorLp")
    fun getCaseByNomorLp(nomorLp: String): LiveData<Case>

    @Query("DELETE FROM case_table")
    fun deleteAll()

    @Delete
    fun deleteCase(case: Case)
}