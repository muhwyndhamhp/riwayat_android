package io.muhwyndhamhp.riwayat.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.muhwyndhamhp.riwayat.dao.CaseDao
import io.muhwyndhamhp.riwayat.dao.CaseNoteDao
import io.muhwyndhamhp.riwayat.dao.MemberDao
import io.muhwyndhamhp.riwayat.database.RoomDatabase
import io.muhwyndhamhp.riwayat.helper.FirebaseHelperImplementation
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class AppRepository(application: Application) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var memberDao: MemberDao?
    private var caseDao: CaseDao?
    private var caseNoteDao: CaseNoteDao?

    private var firebaseHelper: FirebaseHelperImplementation

    init {
        val db = RoomDatabase.getDatabase(application)
        memberDao = db?.memberDao()
        caseDao = db?.caseDao()
        caseNoteDao = db?.caseNoteDao()
        firebaseHelper = FirebaseHelperImplementation()
    }

    /**
     * Below are functions for Case Management
     */

    fun getAllCase() = caseDao?.getAllCase()
    fun getCaseByNomorLp(nomorLp: String) = caseDao?.getCaseByNomorLp(nomorLp)
    fun getCaseByString(string: String) = caseDao?.getCaseByString(string)

    fun insertCase(case: Case) {
        launch { insertCaseBG(case) }
    }

    fun deleteCase(case: Case) {
        launch { deleteCaseBG(case) }
    }


    private suspend fun deleteCaseBG(case: Case) {
        withContext(Dispatchers.IO) {
            caseDao?.deleteCase(case)
        }
        firebaseHelper.deleteCase(case)
    }

    private suspend fun insertCaseBG(case: Case) {
        withContext(Dispatchers.IO) {
            caseDao?.insertCase(case)
        }
        firebaseHelper.uploadCase(case)
    }

    /**
     * Below are functions for Member Management
     */

    fun getAllMember() = memberDao?.getAllMember()

    fun getMember(phoneNumber: String) = memberDao?.getMember(phoneNumber)

    fun setMember(member: Member): MutableLiveData<Constants.Companion.FirebaseUploadStatus> {
        launch { setMemberBG(member) }
        return firebaseHelper.uploadMember(member)
    }

    fun deleteMember(member: Member): MutableLiveData<Constants.Companion.FirebaseUploadStatus> {
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