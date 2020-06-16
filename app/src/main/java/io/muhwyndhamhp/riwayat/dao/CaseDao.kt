package io.muhwyndhamhp.riwayat.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.muhwyndhamhp.riwayat.model.Case

@Dao
interface CaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCase(case: Case)

    @Query("SELECT * FROM case_table ORDER BY timestamp DESC")
    fun getAllCase(): LiveData<MutableList<Case>>

    @Query("SELECT * FROM case_table WHERE nomor_lp = :nomorLp")
    fun getCaseByNomorLp(nomorLp: String): LiveData<Case>

    @Query("SELECT * FROM case_table ORDER BY timestamp DESC LIMIT 5")
    fun getLatestCases(): LiveData<MutableList<Case>>

    @Query("SELECT * FROM case_table WHERE (nomor_lp LIKE :string OR nama_pelapor LIKE :string OR hp_pelapor LIKE :string OR alamat_pelapor LIKE :string OR waktu_kejadian LIKE :string OR latlong_kejadian LIKE :string OR lokasi_kejadian LIKE :string OR lac_cid LIKE :string OR tindak_pidana LIKE :string OR daftar_saksi LIKE :string OR hasil_lidik LIKE :string OR petugas LIKE :string) ORDER BY nomor_lp DESC")
    fun getCaseByString(string: String): LiveData<MutableList<Case>>

    @Query("DELETE FROM case_table")
    fun deleteAll() : Int

    @Delete
    fun deleteCase(case: Case)
}