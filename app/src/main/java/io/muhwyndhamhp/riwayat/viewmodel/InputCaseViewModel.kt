package io.muhwyndhamhp.riwayat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.repository.AppRepository

class InputCaseViewModel(application: Application) : AndroidViewModel(application){

    private var repository = AppRepository(application)

    fun insertCase(case: Case) {
        repository.insertCase(case, true)
    }

    fun getCurrentMember() : LiveData<Member>?{
        val currentMemberPhoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.replace("+62", "0")
        return repository.getMember(currentMemberPhoneNumber)
    }
}