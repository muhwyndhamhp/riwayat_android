package io.muhwyndhamhp.riwayat.model

import androidx.room.*
import io.muhwyndhamhp.riwayat.database.Converters

@Entity(tableName = "case_table")
data class Case (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "nomor_lp") var nomorLP : String = "",
    @ColumnInfo(name = "nama_pelapor") var namaPelapor: String = "",
    @ColumnInfo(name ="hp_pelapor") var hpPelapor : String = "",
    @ColumnInfo(name ="alamat_pelapor") var alamatPelapor: String = "",
    @ColumnInfo(name = "waktu_kejadian") var waktuKejadian: Long = 0L ,
    @ColumnInfo(name= "latlong_kejadian") var latLongKejadian: String ="",
    @ColumnInfo(name = "lokasi_kejadian") var lokasiKejadian: String = "",
    @ColumnInfo(name = "lac_cid") var lacCid : String = "",
    @ColumnInfo(name = "tindak_pidana") var tindakPidana : String = "",
//    @TypeConverters(Converters::class)
    @ColumnInfo(name = "daftar_saksi")
    var daftarSaksi : List<String> = mutableListOf(),
    @ColumnInfo(name = "hasil_lidik") var hasilLidik: String = "",
    @Ignore var caseNotes: List<CaseNote> = mutableListOf()
)