package io.muhwyndhamhp.riwayat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "case_table")
data class Case(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "nomor_lp") var nomorLP: String = "",
    @ColumnInfo(name = "nama_pelapor") var namaPelapor: String = "",
    @ColumnInfo(name = "hp_pelapor") var hpPelapor: String = "",
    @ColumnInfo(name = "alamat_pelapor") var alamatPelapor: String = "",
    @ColumnInfo(name = "waktu_kejadian") var waktuKejadian: Long = 0L,
    @ColumnInfo(name = "latlong_kejadian") var latLongKejadian: String = "",
    @ColumnInfo(name = "lokasi_kejadian") var lokasiKejadian: String = "",
    @ColumnInfo(name = "lac_cid") var lacCid: String = "",
    @ColumnInfo(name = "tindak_pidana") var tindakPidana: String = "",
//    @TypeConverters(Converters::class)
    @ColumnInfo(name = "daftar_saksi")
    var daftarSaksi: List<String> = mutableListOf(),
    @ColumnInfo(name = "hasil_lidik") var hasilLidik: String = "",
    @ColumnInfo(name = "petugas") var petugas: String = "Anonim",
    @ColumnInfo(name = "timestamp") var timestamp: Long = 0L,
    @Ignore var caseNotes: Map<String, CaseNote> = mapOf<String, CaseNote>()
)