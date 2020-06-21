package io.muhwyndhamhp.riwayat.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.CaseNote
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.repository.AppRepository
import kotlinx.coroutines.launch
import java.io.File

class InputCaseViewModel(application: Application) : AndroidViewModel(application) {

    val saksiCounter = MutableLiveData(1)
    val latLong = MutableLiveData<LatLng>()
    lateinit var currentMember: LiveData<Member>
    val currentCase = MutableLiveData<Case>()
    var currentCaseNotes = MutableLiveData(mutableMapOf<String, CaseNote>())

    private val repository = AppRepository(application)

    fun insertCase(case: Case) {
        repository.insertCase(case, true)
    }

    fun getCaseNotes(nomorLP: String) = repository.getCaseNoteByNomorLP(nomorLP)

    fun getMember(): LiveData<Member>? {
        val currentMemberPhoneNumber =
            FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.replace("+62", "0")
        currentMember = repository.getMember(currentMemberPhoneNumber)!!
        return currentMember
    }

    fun getCase(nomorLp: String): LiveData<Case>? = repository.getCaseByNomorLp(nomorLp)
    fun getCase(): MutableLiveData<Case> {
        return currentCase
    }

    fun uploadImages(
        imageLocalRefList: MutableList<String>,
        context: Context,
        lifecycleOwner: LifecycleOwner
    ): MediatorLiveData<List<String>> {
        val downloadURLMediator = MediatorLiveData<List<String>>()
        lifecycleOwner.lifecycleScope.launch {
            val uploadedImages = mutableListOf<String>()
            val compressedImages = mutableListOf<File>()
            for (string in imageLocalRefList) {
                if (string.contains("https")) {
                    uploadedImages.add(string)
                } else {
                    val imageFile = File(string)
                    val compressedImage = Compressor.compress(context, imageFile) {
                        quality(60)
                        size(204_800)
                        format(Bitmap.CompressFormat.JPEG)
                    }
                    compressedImages.add(compressedImage)
                }
            }
            val downloadURLs = mutableListOf<String>()
            downloadURLs.addAll(uploadedImages)
            if (downloadURLs.size == imageLocalRefList.size) {
                downloadURLMediator.postValue(
                    downloadURLs
                )
            } else {
                downloadURLMediator.addSource(repository.uploadImages(compressedImages)) { downloadURL ->
                    if (downloadURL.trim { it <= ' ' }.isNotEmpty()) {
                        downloadURLs.add(downloadURL)
                    }
                    if (downloadURLs.size == (uploadedImages.size + compressedImages.size)) {
                        downloadURLMediator.postValue(downloadURLs)
                    }
                }
            }

        }
        return downloadURLMediator
    }
}