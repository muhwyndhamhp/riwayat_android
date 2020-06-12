package io.muhwyndhamhp.riwayat.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.NOMOR_LP
import io.muhwyndhamhp.riwayat.viewmodel.CaseDetailViewModel
import kotlinx.android.synthetic.main.activity_case_detail.*
import java.text.SimpleDateFormat
import java.util.*

class CaseDetailActivity : AppCompatActivity() {

    private lateinit var currentCase: Case
    private lateinit var viewModel: CaseDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_detail)

        viewModel = ViewModelProvider(this).get(CaseDetailViewModel::class.java)

        inflateInitialContent()
    }

    private fun inflateInitialContent() {
        val currentNomorLP = intent.getStringExtra(NOMOR_LP)
        viewModel.getCase(currentNomorLP!!)!!.observe(this, Observer {
            currentCase = it

            inflateView()
        })
    }

    @SuppressLint("SetTextI18n")
    private fun inflateView() {
        tv_nomor_lp.text = "NO. LP: ${currentCase.nomorLP.replace("-", "/")}"
        tv_nama_pelapor.text = currentCase.namaPelapor
        tv_hp_pelapor.text = currentCase.hpPelapor
        tv_alamat_pelapor.text = currentCase.alamatPelapor
        tv_tindak_pidana.text = currentCase.tindakPidana
        tv_waktu_kejadian.text = "${parseWaktuKejadian()} WIB"
        tv_lokasi_kejadian.text = currentCase.lokasiKejadian
        tv_lokasi_kejadian.setOnClickListener {
            navigateToMaps()
        }
        tv_lac_cid.text = "LAC-CID: ${currentCase.lacCid}"
        tv_petugas.text = "Petugas: ${currentCase.petugas}"
        iv_pidana_icon.setBackgroundResource(selectDrawable(currentCase.tindakPidana))
        bt_delete_case.setOnClickListener { dialogBuilder(this, currentCase) }
        bt_edit_case.setOnClickListener {
            val intent = Intent(this, EditCaseActivity::class.java)
            intent.putExtra(NOMOR_LP, currentCase.nomorLP)
            startActivity(intent)
        }
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            currentCase.daftarSaksi
        )
        lv_saksi.adapter = adapter
        tv_hasil_lidik.text = currentCase.hasilLidik
        bt_add_case_note.setOnClickListener {
            val intent = Intent(this, InputNoteActivity::class.java)
            intent.putExtra(NOMOR_LP, currentCase.nomorLP)
            startActivity(intent)
        }
    }

    private fun dialogBuilder(context: Context, case: Case) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Hapus Kasus")
        builder.setMessage("Apakah anda yakin akan menghapus kasus ini?")
        builder.setPositiveButton("YA") { _, _ ->
            viewModel.deleteCase(case)
            finish()
        }
        builder.setNegativeButton("TIDAK") { _, _ ->
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun selectDrawable(tindakPidana: String): Int {
        return when (tindakPidana) {
            "Pembunuhan" -> R.drawable.ic_pembunuhan
            "Curat" -> R.drawable.ic_curat
            "Curas" -> R.drawable.ic_curas
            "Penganiayaan" -> R.drawable.ic_penganiayaan
            "Curanmor" -> R.drawable.ic_curanmor
            else -> R.drawable.ic_lain
        }
    }

    private fun navigateToMaps() {}

    private fun parseWaktuKejadian(): CharSequence? {
        val date = Date(currentCase.waktuKejadian)
        val locale = Locale("id", "ID")
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, hh.mm", locale)
        return simpleDateFormat.format(date)
    }
}