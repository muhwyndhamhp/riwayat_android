package io.muhwyndhamhp.riwayat.helper

import FirebaseQueryLiveData
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.CaseNote
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.utils.Constants
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.CHILD_ADDED
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.CHILD_CHANGED
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.CHILD_REMOVED
import java.io.File
import java.util.*

class FirebaseHelperImplementation : FirebaseHelper {


    companion object{

        private lateinit var database: DatabaseReference
        private fun initDatabase(): DatabaseReference {
            if (!this::database.isInitialized) {
                Firebase.database.setPersistenceEnabled(true)
                Firebase.database.setPersistenceCacheSizeBytes(41943040)
                val firebaseDatabase = Firebase.database
                database = firebaseDatabase.reference
            }
            return database
        }
    }
    fun enablePersistence(){
        initDatabase()
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

    override fun setUuid(uuid: String, phoneNumber: String) {
        val memberReference = initDatabase().child("members").child(phoneNumber).child("uuid")
        memberReference.setValue(uuid)
    }

    override fun getMember(phoneNumber: String): LiveData<DataSnapshot?> {
        val memberReference = initDatabase().child("members").child(phoneNumber)

        return FirebaseQueryLiveData(memberReference)
    }

    override fun getAllMembers(): MutableLiveData<DataSnapshot?> {


        var gotResult = false
        val liveDataSnapshot = MutableLiveData<DataSnapshot?>()
        val memberReference = initDatabase().child("members")

        val eventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                gotResult = true
                liveDataSnapshot.postValue(p0)
            }

        }
        memberReference.addListenerForSingleValueEvent(eventListener)
        val timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                timer.cancel()
                if (!gotResult) { //  Timeout
                    memberReference.removeEventListener(eventListener)
                    // Your timeout code goes here
                }
            }
        }
        timer.schedule(timerTask, 30000L)

        return liveDataSnapshot
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

    override fun getAllCaseFromServer(): MutableLiveData<DataSnapshot?> {
        val liveDataSnapshot = MutableLiveData<DataSnapshot?>()
        val memberReference = initDatabase().child("cases")

        memberReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                liveDataSnapshot.postValue(p0)
            }

        })
        return liveDataSnapshot
    }

    override fun getCaseChildListener(): MutableLiveData<Pair<String, DataSnapshot>?> {

        val liveDataSnapshot = MutableLiveData<Pair<String, DataSnapshot>?>()
        val memberReference = initDatabase().child("cases")

        memberReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                liveDataSnapshot.postValue(Pair(CHILD_CHANGED, p0))
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                liveDataSnapshot.postValue(Pair(CHILD_ADDED, p0))
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                liveDataSnapshot.postValue(Pair(CHILD_REMOVED, p0))
            }

        })
        return liveDataSnapshot
    }

    override fun uploadImage(imageList: MutableList<File>): MutableLiveData<String> {
        val storage = Firebase.storage.reference.child("caseNoteImages")
        return uploadImageRecurse(storage, imageList)
    }

    override fun uploadCaseNote(caseNote: CaseNote): MutableLiveData<Constants.Companion.FirebaseUploadStatus> {
        val status = MutableLiveData(Constants.Companion.FirebaseUploadStatus.WAITING)
        val memberReference =
            initDatabase().child("cases").child(caseNote.nomorLP).child("caseNotes")
                .child(caseNote.timestamp.toString())
        memberReference
            .setValue(caseNote)
            .addOnSuccessListener { status.postValue(Constants.Companion.FirebaseUploadStatus.COMPLETED) }
            .addOnFailureListener { status.postValue(Constants.Companion.FirebaseUploadStatus.FAILED) }
        return status
    }

    private fun uploadImageRecurse(
        storage: StorageReference,
        imageList: MutableList<File>
    ): MutableLiveData<String> {
        val downloadURLs = MutableLiveData<String>()
        for (a in imageList.indices) {
            val photoRef = storage.child(System.currentTimeMillis().toString() + ".jpg")

            photoRef.putFile(Uri.fromFile(imageList[a])).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                photoRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    downloadURLs.postValue(task.result.toString())
                }
            }
        }
        return downloadURLs
    }
}