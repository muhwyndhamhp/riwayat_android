package io.muhwyndham.riwayat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.muhwyndham.riwayat.R
import io.muhwyndham.riwayat.model.Member
import kotlinx.android.synthetic.main.item_member.view.*

class MemberRVAdapter(private val memberList: List<Member>) :
    RecyclerView.Adapter<MemberRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberRVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(memberList[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(member: Member) {
            itemView.tv_nama_anggota.text = member.memberName
            itemView.tv_nomor_hp.text = member.phoneNumber.toString()
        }
    }


}