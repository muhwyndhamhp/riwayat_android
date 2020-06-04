package io.muhwyndham.riwayat.repository

import android.app.Application
import io.muhwyndham.riwayat.dao.MemberDao
import io.muhwyndham.riwayat.database.RoomDatabase
import io.muhwyndham.riwayat.model.Member
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun deleteMember(member: Member) {
        launch { deleteMemberBG(member) }
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