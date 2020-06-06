package io.muhwyndhamhp.riwayat.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.muhwyndhamhp.riwayat.model.CaseNote

@Dao
interface CaseNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCaseNote(caseNote: CaseNote)

    @Query("SELECT * FROM case_note_table ORDER BY timestamp ASC")
    fun getAllCaseNote(): LiveData<MutableList<CaseNote>>

    @Query("SELECT * FROM case_note_table WHERE nomor_lp = :nomorLp")
    fun getCaseNoteByNomorLp(nomorLp: String): LiveData<MutableList<CaseNote>>

    @Query("DELETE FROM case_note_table")
    fun deleteAllCaseNote()

    @Delete
    fun deleteCaseNote(caseNote: CaseNote)

}