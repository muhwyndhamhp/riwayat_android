package io.muhwyndhamhp.riwayat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.muhwyndhamhp.riwayat.repository.AppRepository

class InputCaseViewModel(application: Application) : AndroidViewModel(application){

    private var repository = AppRepository(application)

}