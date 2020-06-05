package io.muhwyndhamhp.riwayat.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.muhwyndhamhp.riwayat.dao.MemberDao
import io.muhwyndhamhp.riwayat.database.RoomDatabase
import io.muhwyndhamhp.riwayat.model.Member
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MemberRepository(application: Application) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var memberDao: MemberDao?

    init {
        val db = RoomDatabase.getDatabase(application)
        memberDao = db?.memberDao()
    }

    fun getAllMember() = memberDao?.getAllMember()

    fun getMember(phoneNumber: String) = memberDao?.getMember(phoneNumber)

    fun setMember(member: Member) {
        launch { setMemberBG(member) }
    }

    fun deleteMember(member: Member) : MutableLiveData<Boolean>{
        launch { deleteMemberBG(member) }

        return MutableLiveData(true)
    }


    private suspend fun fakeDelayAsync() : MutableLiveData<Boolean> {
        return MutableLiveData(true)
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