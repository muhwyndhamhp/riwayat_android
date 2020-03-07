package io.muhwyndham.riwayat.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.muhwyndham.riwayat.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        cv_anggota.setOnClickListener {
            startActivity(
                Intent(
                    this@HomeActivity,
                    ManageMemberActivity::class.java
                )
            )
        }
    }
}
