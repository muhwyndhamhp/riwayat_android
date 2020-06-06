package io.muhwyndhamhp.riwayat.helper

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.utils.Constants

class FirebaseHelperImplementation : FirebaseHelper {
    private lateinit var database: DatabaseReference

    private fun initDatabase(): DatabaseReference {
        if (!this::database.isInitialized) {
            val firebaseDatabase = Firebase.database
            database = firebaseDatabase.reference
        }
        return database
    }

    override fun uploadMember(member: Member): MutableLiveData<Constants.Companion.FirebaseUploadStatus> {
        val status = MutableLiveData(Constants.Companion.FirebaseUploadStatus.WAITING)
        val memberReference = initDatabase().child("members").child(member.phoneNumber)
        memberReference
            .setValue(member)
            .addOnSuccessListener { status.postValue(Constants.Companion.FirebaseUploadStatus.COMPLETED) }
            .addOnFailureListener { status.postValue(Constants.Companion.FirebaseUploadStatus.FAILED) }
        return status
    }

    override fun deleteMember(member: Member): MutableLiveData<Constants.Companion.FirebaseUploadStatus> {
        val status = MutableLiveData(Constants.Companion.FirebaseUploadStatus.WAITING)
        val memberReference = initDatabase().child("members").child(member.phoneNumber)
        memberReference
            .setValue(null)
            .addOnSuccessListener { status.postValue(Constants.Companion.FirebaseUploadStatus.COMPLETED) }
            .addOnFailureListener { status.postValue(Constants.Companion.FirebaseUploadStatus.FAILED) }
        return status
    }

    override fun uploadCase(case: Case): MutableLiveData<Constants.Companion.FirebaseUploadStatus> {
        val status = MutableLiveData(Constants.Companion.FirebaseUploadStatus.WAITING)
        val memberReference = initDatabase().child("cases").child(case.nomorLP)
        memberReference
            .setValue(case)
            .addOnSuccessListener { status.postValue(Constants.Companion.FirebaseUploadStatus.COMPLETED) }
            .addOnFailureListener { status.postValue(Constants.Companion.FirebaseUploadStatus.FAILED) }
        return status
    }

    override fun deleteCase(case: Case): MutableLiveData<Constants.Companion.FirebaseUploadStatus> {
        val status = MutableLiveData(Constants.Companion.FirebaseUploadStatus.WAITING)
        val memberReference = initDatabase().child("cases").child(case.nomorLP)
        memberReference
            .setValue(null)
            .addOnSuccessListener { status.postValue(Constants.Companion.FirebaseUploadStatus.COMPLETED) }
            .addOnFailureListener { status.postValue(Constants.Companion.FirebaseUploadStatus.FAILED) }
        return status
    }

}