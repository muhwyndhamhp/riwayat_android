package io.muhwyndhamhp.riwayat.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.CaseNote
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.utils.Constants
import java.io.File

interface FirebaseHelper {
    fun uploadMember(member: Member) : MutableLiveData<Constants.Companion.FirebaseUploadStatus>

    fun deleteMember(member: Member) : MutableLiveData<Constants.Companion.FirebaseUploadStatus>

    fun setUuid (uuid: String, phoneNumber: String)

    fun getMember(phoneNumber: String) : LiveData<DataSnapshot?>

    fun getAllMembers() : MutableLiveData<DataSnapshot?>

    fun uploadCase(case: Case) : MutableLiveData<Constants.Companion.FirebaseUploadStatus>

    fun deleteCase(case: Case) : MutableLiveData<Constants.Companion.FirebaseUploadStatus>

    fun getAllCaseFromServer() : MutableLiveData<DataSnapshot?>

    fun getCaseChildListener() : MutableLiveData<Pair<String, DataSnapshot>?>

    fun uploadImage(imageList : MutableList<File>) : MutableLiveData<String>

    fun uploadCaseNote(caseNote: CaseNote) : MutableLiveData<Constants.Companion.FirebaseUploadStatus>
}