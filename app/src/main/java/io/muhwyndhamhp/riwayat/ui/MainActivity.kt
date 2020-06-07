package io.muhwyndhamhp.riwayat.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.repository.AppRepository
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.RC_SIGN_IN
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Firebase.database.setPersistenceEnabled(true)
        Firebase.database.setPersistenceCacheSizeBytes(41943040)
        prepareFirebaseUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {

                val toast = Toast.makeText(
                    this@MainActivity,
                    "Login berhasil!",
                    Toast.LENGTH_LONG
                )

                toast.show()
                afterLoginDataFetch()

            } else {
                val toast = Toast.makeText(
                    this@MainActivity,
                    "Login gagal, silahkan coba lagi",
                    Toast.LENGTH_LONG
                )

                toast.show()
            }
        }
    }

    private fun afterLoginDataFetch() {
        val repository = AppRepository(this@MainActivity.application)


        member_status.text = "Mengecek status keanggotaan..."

        repository.getAllMemerFromServer().observe(this@MainActivity, Observer {
            var isMember = false
            for (member in it) {
                val firebasePhoneNumber =
                    FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.replace("+62", "0")
                if (member.phoneNumber == firebasePhoneNumber) isMember =
                    true
                repository.setMember(member)
            }

            if (isMember) {
                member_status.text = "Keanggotaan tervalidasi, mendownload data..."
                repository.getAllCaseFromServer().observe(this, Observer { caseList ->
                    repository.refreshAllCase(caseList).observe(this, Observer { isRefreshed ->
                        if (isRefreshed) {
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        }
                    })

                })
            } else {
                member_status.text =
                    "Anda bukan Anggota kepolisian Kabupaten Bantul, jika anda merasa anggota, silahkan hubungi Admin 'Riwayat'"
                FirebaseAuth.getInstance().signOut()
            }

        })
    }

    private fun prepareFirebaseUI() {
        val providers = arrayListOf(AuthUI.IdpConfig.PhoneBuilder().build())

        if (FirebaseAuth.getInstance().currentUser == null)
            startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                    providers
                ).build(), RC_SIGN_IN
            )
        else afterLoginDataFetch()
    }
}
