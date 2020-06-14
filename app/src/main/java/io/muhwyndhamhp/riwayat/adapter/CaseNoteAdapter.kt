package io.muhwyndhamhp.riwayat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.model.CaseNote
import kotlinx.android.synthetic.main.item_case_note.view.*
import java.text.SimpleDateFormat
import java.util.*

class CaseNoteAdapter(val context: Context, val caseNoteList: List<CaseNote>) :
    RecyclerView.Adapter<CaseNoteAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(caseNote: CaseNote, index: Int, context: Context) {
            itemView.tv_waktu_kejadian.text = parseWaktuKejadian(caseNote.timestamp)
            itemView.tv_judul_catatan.text = caseNote.judulCatatan
            if (caseNote.isiCatatan != "") {
                itemView.tv_body_catatan.text = caseNote.isiCatatan
            } else {
                itemView.ll_body_catatan.visibility = View.GONE
            }
            if (caseNote.daftarGambar.isNotEmpty()) {
                val a = itemView
                val ivList = listOf(a.iv_1, a.iv_2, a.iv_3, a.iv_4, a.iv_5, a.iv_6)
                val ccList = listOf(a.cc_1, a.cc_2, a.cc_3, a.cc_4, a.cc_5, a.cc_6)

                for (i in caseNote.daftarGambar.indices) {
                    Glide.with(context).load(caseNote.daftarGambar[i]).into(ivList[i])
                    ccList[i].visibility = View.VISIBLE
                    ccList[i].setOnClickListener {
                        StfalconImageViewer.Builder<String>(context, caseNote.daftarGambar){view, imageUrl ->
                            Glide.with(context).load(imageUrl).into(view)
                        }.withStartPosition(i).show()
                    }
                }
            }
        }

        private fun parseWaktuKejadian(timestamp: Long): String {
            val date = Date(timestamp)
            val locale = Locale("id", "ID")
            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH.mm", locale)
            return simpleDateFormat.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_case_note, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = caseNoteList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(caseNoteList[position], position, context)
    }

}