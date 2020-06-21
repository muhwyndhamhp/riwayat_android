package io.muhwyndhamhp.riwayat.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.adapter.CaseNoteAdapter
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.NOMOR_LP
import io.muhwyndhamhp.riwayat.viewmodel.CaseDetailViewModel
import kotlinx.android.synthetic.main.activity_case_detail.*
import java.text.SimpleDateFormat
import java.util.*

class CaseDetailActivity : AppCompatActivity() {

    private lateinit var currentCase: Case
    private lateinit var viewModel: CaseDetailViewModel

    private lateinit var adapter: CaseNoteAdapter

    private val mLacCidList = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_detail)

        viewModel = ViewModelProvider(this).get(CaseDetailViewModel::class.java)

        mLacCidList.addAll(
            mutableListOf(tv_lac_cid_1, tv_lac_cid_2, tv_lac_cid_3, tv_lac_cid_4, tv_lac_cid_5)
        )
        inflateInitialContent()

    }

    private fun inflateInitialContent() {
        val currentNomorLP = intent.getStringExtra(NOMOR_LP)
        viewModel.getCase(currentNomorLP!!)!!.observe(this, Observer {
            currentCase = it

            inflateView()

            inflateCaseNote()
        })
    }

    private fun inflateCaseNote() {
        viewModel.getCaseNotes(currentCase.nomorLP)!!.observe(this, Observer {
            adapter = CaseNoteAdapter(this, it)
            rv_case_note.adapter = adapter
            rv_case_note.layoutManager = LinearLayoutManager(this)
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
        for (i in currentCase.lacCid.indices) {
            mLacCidList[i].text = "LAC-CID : ${currentCase.lacCid[i]}"
            mLacCidList[i].visibility = View.VISIBLE
        }
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

        val ivList = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6)
        val ccList = listOf(cc_1, cc_2, cc_3, cc_4, cc_5, cc_6)
        for (i in currentCase.imageUrls.indices) {
            Glide.with(this).load(currentCase.imageUrls[i]).into(ivList[i])
            ccList[i].visibility = View.VISIBLE
            ccList[i].setOnClickListener {
                StfalconImageViewer.Builder<String>(this, currentCase.imageUrls) { view, imageUrl ->
                    Glide.with(this).load(imageUrl).into(view)
                }.withStartPosition(i).show()
            }
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
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH.mm", locale)
        return simpleDateFormat.format(date)
    }
}
