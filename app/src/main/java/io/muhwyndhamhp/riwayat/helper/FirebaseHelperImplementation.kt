package io.muhwyndhamhp.riwayat.helper

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.muhwyndhamhp.riwayat.model.Member

class FirebaseHelperImplementation : FirebaseHelper {
    private lateinit var database: DatabaseReference

    private fun initDatabase(): DatabaseReference {
        if (!this::database.isInitialized) {
            val firebaseDatabase = Firebase.database
            firebaseDatabase.setPersistenceEnabled(true)
            firebaseDatabase.setPersistenceCacheSizeBytes(41943040)
            database = firebaseDatabase.reference
        }
        return database
    }

    override fun uploadMember(member: Member): MutableLiveData<FirebaseUploadStatus> {
        val status = MutableLiveData(FirebaseUploadStatus.WAITING)
        val memberReference = initDatabase().child("members").child(member.phoneNumber)
        memberReference
            .setValue(member)
            .addOnSuccessListener { status.postValue(FirebaseUploadStatus.COMPLETED) }
            .addOnFailureListener { status.postValue(FirebaseUploadStatus.FAILED) }
        return status
    }

    override fun deleteMember(member: Member): MutableLiveData<FirebaseUploadStatus> {
        val status = MutableLiveData(FirebaseUploadStatus.WAITING)
        val memberReference = initDatabase().child("members").child(member.phoneNumber)
        memberReference
            .setValue(null)
            .addOnSuccessListener { status.postValue(FirebaseUploadStatus.COMPLETED) }
            .addOnFailureListener { status.postValue(FirebaseUploadStatus.FAILED) }
        return status
    }

}