package io.muhwyndhamhp.riwayat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.model.Case
import kotlinx.android.synthetic.main.item_case.view.*

class CaseListRVAdapter(val context: Context, private val caseList: List<Case>) :
    RecyclerView.Adapter<CaseListRVAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(case: Case, context: Context, index: Int) {
            itemView.tv_nomor_lp.text = "Nomor LP: " + case.nomorLP.replace("-", "/")
            itemView.tv_nama_pelapor.text = "Pelapor:\n"+ case.namaPelapor
            itemView.tv_lokasi_kejadian.text = "Lokasi kejadian:\n" + case.lokasiKejadian
            itemView.tv_tindak_pidana.text = case.tindakPidana
            itemView.iv_pidana_icon.setBackgroundResource(selectDrawable(case.tindakPidana))
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