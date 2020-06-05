package io.muhwyndhamhp.riwayat.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.ui.ManageMemberActivity
import kotlinx.android.synthetic.main.item_member.view.*

class MemberRVAdapter(val context: Context, private val memberList: List<Member>) :
    RecyclerView.Adapter<MemberRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberRVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(memberList[position], context, position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(
            member: Member,
            context: Context,
            position: Int
        ) {
            itemView.tv_nama_anggota.text = member.memberName
            itemView.tv_nomor_hp.text = member.phoneNumber
            if(position%2 == 0) {
                itemView.item_layout.setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.light_gray, null))
                itemView.bt_delete_member.setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.light_gray, null))
            }
            itemView.bt_delete_member.setOnClickListener {
                (context as ManageMemberActivity).deleteMember(member)
            }
        }
    }


}