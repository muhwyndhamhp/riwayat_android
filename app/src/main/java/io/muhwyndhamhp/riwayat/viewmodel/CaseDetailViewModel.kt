package io.muhwyndhamhp.riwayat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.repository.AppRepository

class CaseDetailViewModel(application: Application): AndroidViewModel(application) {

    val repository = AppRepository(application)

    fun getCase(nomorLP : String) = repository.getCaseByNomorLp(nomorLP)
    fun deleteCase(case: Case) = repository.deleteCase(case)
    fun getCaseNotes(nomorLP: String) = repository.getCaseNoteByNomorLP(nomorLP)


}