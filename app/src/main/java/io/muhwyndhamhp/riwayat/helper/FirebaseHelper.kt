package io.muhwyndhamhp.riwayat.helper

import androidx.lifecycle.MutableLiveData
import io.muhwyndhamhp.riwayat.model.Member

interface FirebaseHelper {
    fun uploadMember(member: Member) : MutableLiveData<FirebaseUploadStatus>

    fun deleteMember(member: Member) : MutableLiveData<FirebaseUploadStatus>
}