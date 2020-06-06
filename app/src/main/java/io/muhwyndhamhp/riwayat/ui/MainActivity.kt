package io.muhwyndhamhp.riwayat.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.RC_SIGN_IN

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

                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
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

    private fun prepareFirebaseUI() {
        val providers = arrayListOf(AuthUI.IdpConfig.PhoneBuilder().build())

        if (FirebaseAuth.getInstance().currentUser == null)
            startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                    providers
                ).build(), RC_SIGN_IN
            )
        else startActivity(Intent(this@MainActivity, HomeActivity::class.java))
    }
}
