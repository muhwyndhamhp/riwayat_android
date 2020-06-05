package io.muhwyndhamhp.riwayat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import io.muhwyndhamhp.riwayat.database.Converters

@Entity(tableName = "case_note_table")
data class CaseNote (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "nomor_lp") var nomorLP : String = "",
    @ColumnInfo(name = "judul_catatan") var judulCatatan: String = "",
    @ColumnInfo(name = "isi_catatan") var isiCatatan : String = "",
//    @TypeConverters(Converters::class)
    @ColumnInfo(name = "daftar_gambar")
    var daftarGambar: List<String> = mutableListOf()
)