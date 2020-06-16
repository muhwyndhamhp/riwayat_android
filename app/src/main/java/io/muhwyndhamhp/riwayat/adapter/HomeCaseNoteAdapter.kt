package io.muhwyndhamhp.riwayat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.ui.CaseDetailActivity
import io.muhwyndhamhp.riwayat.ui.HomeActivity
import io.muhwyndhamhp.riwayat.utils.Constants
import kotlinx.android.synthetic.main.item_interactive_content.view.*

class HomeCaseNoteAdapter(val context: Context, private val newestCases: List<Case>) :
    RecyclerView.Adapter<HomeCaseNoteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindView(case: Case, position: Int, context: Context) {
            itemView.tv_nomor_lp.text = "No. LP:\n${case.nomorLP.replace("-", "/")}"
            itemView.tv_tindak_pidana.text = case.tindakPidana
            itemView.tv_nama_petugas.text = case.petugas
            itemView.iv_pidana_icon.setBackgroundResource(selectDrawable(case.tindakPidana))
            itemView.setOnClickListener {
                val intent = Intent(context, CaseDetailActivity::class.java)
                intent.putExtra(Constants.NOMOR_LP, case.nomorLP)
                (context as HomeActivity).startActivity(intent)
            }
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
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_interactive_content, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = newestCases.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(newestCases[position], position, context)
    }
}