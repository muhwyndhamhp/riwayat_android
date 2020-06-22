package io.muhwyndhamhp.riwayat.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.ui.CaseDetailActivity
import io.muhwyndhamhp.riwayat.ui.CaseListActivity
import io.muhwyndhamhp.riwayat.ui.EditCaseActivity
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.NOMOR_LP
import kotlinx.android.synthetic.main.item_case.view.*

class CaseListRVAdapter(val context: Context, val caseList: MutableList<Case>) :
    RecyclerView.Adapter<CaseListRVAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindView(case: Case, context: Context, index: Int) {
            itemView.tv_nomor_lp.text = "Nomor LP: " + case.nomorLP.replace("-", "/")
            itemView.tv_nama_pelapor.text = "Pelapor:\n" + case.namaPelapor
            itemView.tv_lokasi_kejadian.text = "Lokasi kejadian:\n" + case.lokasiKejadian
            itemView.tv_tindak_pidana.text = case.tindakPidana
            itemView.iv_pidana_icon.apply {
                setBackgroundResource(selectDrawable(case.tindakPidana))
            }
            itemView.bt_edit_case.setOnClickListener {
                val intent = Intent(context, EditCaseActivity::class.java)
                intent.putExtra(NOMOR_LP, case.nomorLP)
                (context as CaseListActivity).startActivity(intent)
            }
            itemView.bt_delete_case.setOnClickListener { dialogBuilder(context, case) }
            itemView.tv_petugas.text = "Petugas: " + case.petugas
            itemView.setOnClickListener {
                val intent = Intent(context, CaseDetailActivity::class.java)
                intent.putExtra(NOMOR_LP, case.nomorLP)
                (context as CaseListActivity).startActivity(intent)
            }

            if (case.imageUrls.isNotEmpty()) {
                val a = itemView
                val ivList = listOf(a.iv_1, a.iv_2, a.iv_3, a.iv_4, a.iv_5, a.iv_6)
                val ccList = listOf(a.cc_1, a.cc_2, a.cc_3, a.cc_4, a.cc_5, a.cc_6)

                for (i in case.imageUrls.indices) {
                    Glide.with(context).load(case.imageUrls[i]).into(ivList[i])
                    ccList[i].visibility = View.VISIBLE
                    ccList[i].setOnClickListener {
                        StfalconImageViewer.Builder<String>(
                            context,
                            case.imageUrls
                        ) { view, imageUrl ->
                            Glide.with(context).load(imageUrl).into(view)
                        }.withStartPosition(i).show()
                    }
                }
            }
        }

        private fun selectColor(case: Case): String {
            return when (case.tindakPidana) {
                "Pembunuhan" -> "#6202EE"
                "Curat" -> "#EF00B0"
                "Curas" -> "#FF0072"
                "Penganiayaan" -> "#FF7444"
                "Curanmor" -> "#FFBE3B"
                else -> "#F9F871"
            }
        }

        private fun dialogBuilder(context: Context, case: Case) {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Hapus Kasus")
            builder.setMessage("Apakah anda yakin akan menghapus kasus ini?")
            builder.setPositiveButton("YA") { _, _ ->
                (context as CaseListActivity).caseListViewModel!!.deleteCase(case)
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_case, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = caseList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(caseList[position], context, position)
    }
}