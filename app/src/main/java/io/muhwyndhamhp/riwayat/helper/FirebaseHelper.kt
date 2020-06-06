package io.muhwyndhamhp.riwayat.helper

import androidx.lifecycle.MutableLiveData
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.utils.Constants

interface FirebaseHelper {
    fun uploadMember(member: Member) : MutableLiveData<Constants.Companion.FirebaseUploadStatus>

    fun deleteMember(member: Member) : MutableLiveData<Constants.Companion.FirebaseUploadStatus>

    fun uploadCase(case: Case) : MutableLiveData<Constants.Companion.FirebaseUploadStatus>

    fun deleteCase(case: Case) : MutableLiveData<Constants.Companion.FirebaseUploadStatus>
}