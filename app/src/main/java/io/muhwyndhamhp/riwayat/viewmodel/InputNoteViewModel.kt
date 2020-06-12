package io.muhwyndhamhp.riwayat.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.*
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import io.muhwyndhamhp.riwayat.model.CaseNote
import io.muhwyndhamhp.riwayat.repository.AppRepository
import io.muhwyndhamhp.riwayat.ui.InputNoteActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class InputNoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    fun uploadImages(
        imageLocalRefList: MutableList<String>,
        context: Context,
        lifecycleOwner: LifecycleOwner
    ): MediatorLiveData<List<String>> {
        val downloadURLMediator = MediatorLiveData<List<String>>()
        (context as InputNoteActivity).lifecycleScope.launch {
            val compressedImages = mutableListOf<File>()
            for (string in imageLocalRefList) {
                val imageFile = File(string)
                val compressedImage = Compressor.compress(context, imageFile, Dispatchers.Main) {
                    quality(60)
                    size(204_800)
                    format(Bitmap.CompressFormat.JPEG)
                }
                compressedImages.add(compressedImage)
            }
            val downloadURLs = mutableListOf<String>()
            downloadURLMediator.addSource(repository.uploadImages(compressedImages)) { downloadURL ->
                if (downloadURL.trim { it <= ' ' }.isNotEmpty()) {
                    downloadURLs.add(downloadURL)
                }
                if (downloadURLs.size == compressedImages.size) {
                    downloadURLMediator.postValue(downloadURLs)
                }
            }
        }
        return downloadURLMediator
    }

    fun getCase(nomorLP: String) = repository.getCaseByNomorLp(nomorLP)
    fun getCurrentUser(replace: String)  = repository.getMember(replace)
    fun insertCaseNote(caseNote: CaseNote) { repository.uploadCaseNote(caseNote, true)

    }
}