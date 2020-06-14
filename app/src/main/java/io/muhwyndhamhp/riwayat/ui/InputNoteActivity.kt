package io.muhwyndhamhp.riwayat.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.opensooq.supernova.gligar.GligarPicker
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.CaseNote
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.GLIGAR_PICKER
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.NOMOR_LP
import io.muhwyndhamhp.riwayat.viewmodel.InputNoteViewModel
import kotlinx.android.synthetic.main.activity_input_note.*

class InputNoteActivity : AppCompatActivity() {

    private var imageLocalRefList: MutableList<String> = mutableListOf()
    private lateinit var ivList: List<ImageView>
    private lateinit var ibList: List<ImageButton>
    private lateinit var viewModel: InputNoteViewModel
    private lateinit var currentCase: Case
    private lateinit var currentMember: Member

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_note)

        viewModel = ViewModelProvider(this).get(InputNoteViewModel::class.java)

        getCaseFromIntent()
        getCurrentUser()
        ivList = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6)
        ibList = listOf(ib_1, ib_2, ib_3, ib_4, ib_5, ib_6)
        initiateImagePicker()
        setNoteUploadProcess()
    }

    private fun getCurrentUser() {
        viewModel.getCurrentUser(
            FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.replace(
                "+62",
                "0"
            )
        )!!.observe(this, Observer {
            currentMember = it
        })
    }

    private fun getCaseFromIntent() {
        val nomorLP = intent.extras!!.getString(NOMOR_LP)
        viewModel.getCase(nomorLP!!)!!.observe(this, Observer {
            currentCase = it
        })
    }

    private fun setNoteUploadProcess() {
        bt_add_note.setOnClickListener {
            if (eT_title_note.text.toString().trim { it <= ' ' }
                    .isNotEmpty()) {
                if (et_body_note.text.toString().trim { it <= ' ' }
                        .isNotEmpty() || imageLocalRefList.isNotEmpty()) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progress_upload.visibility = View.VISIBLE

                    if(imageLocalRefList.isNotEmpty())
                    {
                        tv_process.text = "Mengupload gambar..."
                        tv_process.visibility = View.VISIBLE
                        viewModel.uploadImages(imageLocalRefList, this, this)
                            .observe(this, Observer { downloadURLs ->
                                tv_process.text = "Menambahkan catatan..."
                                tv_process.visibility = View.VISIBLE
                                if (downloadURLs.isNotEmpty()) {
                                    val caseNote = CaseNote(
                                        System.currentTimeMillis(),
                                        currentCase.nomorLP,
                                        eT_title_note.text.toString(),
                                        et_body_note.text.toString(),
                                        currentMember.memberName,
                                        downloadURLs
                                    )

                                    viewModel.insertCaseNote(caseNote)
                                    finish()
                                }
                            })
                    }
                    else {
                        tv_process.text = "Menambahkan catatan..."
                        tv_process.visibility = View.VISIBLE
                        val caseNote = CaseNote(
                            System.currentTimeMillis(),
                            currentCase.nomorLP,
                            eT_title_note.text.toString(),
                            et_body_note.text.toString(),
                            currentMember.memberName
                        )
                        viewModel.insertCaseNote(caseNote)
                        finish()
                    }
                } else {
                    showToast("Harus ada catatan / gambar yang dimuat!")
                }
            } else {
                showToast("Kolom judul tidak boleh kosong!")
            }

        }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.show()
    }

    private fun initiateImagePicker() {
        bt_add_image.setOnClickListener {
            GligarPicker().requestCode(GLIGAR_PICKER).limit(6 - imageLocalRefList.size)
                .withActivity(this).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GLIGAR_PICKER) {
            if(resultCode == Activity.RESULT_OK){
                val imagesList = data?.extras!!.getStringArray(GligarPicker.IMAGES_RESULT)
                if (!imagesList.isNullOrEmpty()) {
                    imageLocalRefList.addAll(imagesList.toList())

                    if (imageLocalRefList.isNotEmpty()) {
                        setImageAsset()
                    }
                }
            }
        }
    }

    private fun setImageAsset() {
        if (imageLocalRefList.size == 6) bt_add_image.visibility = View.INVISIBLE
        else bt_add_image.visibility = View.VISIBLE
        for (i in imageLocalRefList.indices) {
            Glide.with(this).load(imageLocalRefList[i])
                .into(ivList[i])
            ivList[i].visibility = View.VISIBLE
            ibList[i].visibility = View.VISIBLE
            ibList[i].setOnClickListener {
                imageLocalRefList.removeAt(i)
                ivList[imageLocalRefList.lastIndex + 1].visibility = View.GONE
                ibList[imageLocalRefList.lastIndex + 1].visibility = View.GONE
                setImageAsset()
            }
        }

    }
}
