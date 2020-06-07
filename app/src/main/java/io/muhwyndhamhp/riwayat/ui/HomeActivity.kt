package io.muhwyndhamhp.riwayat.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.repository.AppRepository
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        val imageViewTarget = GlideDrawableImageViewTarget(iv_)
        Glide.with(this).load(R.raw.one).into(iv_background)

        cv_anggota.setOnClickListener {
            startActivity(
                Intent(
                    this@HomeActivity,
                    ManageMemberActivity::class.java
                )
            )
        }

        cv_daftar_kasus.setOnClickListener {
            startActivity(Intent(this@HomeActivity, CaseListActivity::class.java))
        }

        cv_input_kasus.setOnClickListener {
            startActivity(
                Intent(
                    this@HomeActivity,
                    InputCaseActivity::class.java
                )
            )
        }

        getCurrentUser()

    }

    private fun getCurrentUser() {
        val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.replace("+62", "0")
        AppRepository(this.application).getMember(currentUserPhoneNumber)!!.observe(this, Observer {
            text_current_user.text = "Selamat datang,\n${it.memberName}!"
        })
    }
}
