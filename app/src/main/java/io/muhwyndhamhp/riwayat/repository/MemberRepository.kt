package io.muhwyndhamhp.riwayat.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.muhwyndhamhp.riwayat.dao.MemberDao
import io.muhwyndhamhp.riwayat.database.RoomDatabase
import io.muhwyndhamhp.riwayat.helper.FirebaseHelper
import io.muhwyndhamhp.riwayat.helper.FirebaseHelperImplementation
import io.muhwyndhamhp.riwayat.helper.FirebaseUploadStatus
import io.muhwyndhamhp.riwayat.model.Member
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MemberRepository(application: Application) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var memberDao: MemberDao?
    private lateinit var firebaseHelper : FirebaseHelperImplementation

    init {
        val db = RoomDatabase.getDatabase(application)
        memberDao = db?.memberDao()
        firebaseHelper = FirebaseHelperImplementation()
    }

    fun getAllMember() = memberDao?.getAllMember()

    fun getMember(phoneNumber: String) = memberDao?.getMember(phoneNumber)

    fun setMember(member: Member) : MutableLiveData<FirebaseUploadStatus>{
        launch { setMemberBG(member) }
        return firebaseHelper.uploadMember(member)
    }

    fun deleteMember(member: Member) : MutableLiveData<FirebaseUploadStatus>{
        launch { deleteMemberBG(member) }
        return firebaseHelper.deleteMember(member)
    }

    private suspend fun deleteMemberBG(member: Member) {
        withContext(Dispatchers.IO) {
            memberDao?.delete(member)
        }
    }

    private suspend fun setMemberBG(member: Member) {
        withContext(Dispatchers.IO) {
            memberDao?.setMember(member)

        }
    }
}