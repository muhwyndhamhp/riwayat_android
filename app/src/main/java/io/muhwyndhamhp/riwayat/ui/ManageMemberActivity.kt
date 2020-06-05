package io.muhwyndhamhp.riwayat.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.adapter.MemberRVAdapter
import io.muhwyndhamhp.riwayat.helper.FirebaseUploadStatus
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.viewmodel.ManageMemberViewModel
import kotlinx.android.synthetic.main.activity_manage_member.*

class ManageMemberActivity : AppCompatActivity() {

    private var manageMemberViewModel: ManageMemberViewModel? = null

    private var adapter: MemberRVAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_member)

        manageMemberViewModel = ViewModelProvider(this).get(ManageMemberViewModel::class.java)

        bt_input_member.setOnClickListener {
            manageMemberViewModel?.insertMember(
                et_member_name.text.toString(),
                et_member_phone.text.toString()
            )?.observe(this@ManageMemberActivity, Observer { insertStatus ->
                when (insertStatus) {
                    FirebaseUploadStatus.COMPLETED -> setPostInsertedState()
                    FirebaseUploadStatus.WRONG_INPUT -> showToast("Kolom tidak boleh kosong!")
                    FirebaseUploadStatus.FAILED -> showToast("Gagal mengupload data, mengulang...")
                    FirebaseUploadStatus.CONFLICT -> showToast("Anggota dengan nomor telepon tersebut sudah ada!")
                    else -> showToast("Mengupload data...")
                }
            })
        }

        manageMemberViewModel?.getMemberList()?.observe(this, Observer { memberList ->
            renderMember(memberList)
        })
    }

    private fun setPostInsertedState() {
        et_member_name.setText("")
        et_member_phone.setText("")
        showToast("Berhasil menginput anggota!")
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(this@ManageMemberActivity, message, Toast.LENGTH_LONG)
        toast.show()
    }

    private fun renderMember(memberList: MutableList<Member>) {
        if (memberList.isEmpty()) {
            adapter = MemberRVAdapter(
                this,
                listOf(
                    Member(
                        "Silahkan masukkan anggota baru melalui input dibawah",
                        "Tidak ada Anggota!"
                    )
                )
            )
        } else {
            adapter = MemberRVAdapter(this, memberList)
        }
        val layoutManager = LinearLayoutManager(this@ManageMemberActivity)
        rv_member.adapter = adapter
        rv_member.layoutManager = layoutManager
    }

    /**
     * later don't forget to move this logic to view model!
     */
    fun deleteMember(member: Member) {
        manageMemberViewModel?.deleteMember(member)?.observe(
            this,
            Observer { uploadStatus ->
                when (uploadStatus) {
                    FirebaseUploadStatus.COMPLETED -> showToast("Anggota sudah dihapus!")
                    FirebaseUploadStatus.FAILED -> showToast("Gagal hapus data, mengulang...")
                    else -> showToast("Menghapus data...")
                }
            })
    }
}
