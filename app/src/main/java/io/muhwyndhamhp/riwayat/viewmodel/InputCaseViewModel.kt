package io.muhwyndhamhp.riwayat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.repository.AppRepository

class InputCaseViewModel(application: Application) : AndroidViewModel(application){

    private var repository = AppRepository(application)

    fun insertCase(case: Case) {
        repository.insertCase(case, true)
    }
}