package io.muhwyndhamhp.riwayat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.CaseNote
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.repository.AppRepository

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
}