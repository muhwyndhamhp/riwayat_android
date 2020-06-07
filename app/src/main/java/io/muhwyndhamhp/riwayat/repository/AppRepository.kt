package io.muhwyndhamhp.riwayat.repository

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.ktx.getValue
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

    fun getAllCaseFromServer(): MediatorLiveData<List<Case>> {
        val snapShotConverter = MediatorLiveData<List<Case>>()

        snapShotConverter.addSource(firebaseHelper.getAllCaseFromServer()) { allMemberSnapshot ->
            allMemberSnapshot?.let {
                val result = mutableListOf<Case>()
                for (snapshot in allMemberSnapshot.children) {
                    result.add(snapshot.getValue<Case>()!!)
                }
                snapShotConverter.postValue(result)
            }
        }
        return snapShotConverter
    }

    fun insertCase(case: Case, isWithServer: Boolean) {
        launch { insertCaseBG(case, isWithServer) }
    }

    fun deleteCase(case: Case) {
        launch { deleteCaseBG(case) }
    }

    fun deleteAllCase() {
        launch { deleteAllCaseBG() }
    }

    private suspend fun deleteAllCaseBG() {
        withContext(Dispatchers.IO){
            caseDao?.deleteAll()
        }
    }

    private suspend fun deleteCaseBG(case: Case) {
        withContext(Dispatchers.IO) {
            caseDao?.deleteCase(case)
        }
        firebaseHelper.deleteCase(case)
    }

    private suspend fun insertCaseBG(case: Case, isWithServer: Boolean) {
        withContext(Dispatchers.IO) {
            caseDao?.insertCase(case)
        }
        if(isWithServer)
        firebaseHelper.uploadCase(case)
    }

    /**
     * Below are functions for Member Management
     */

    fun setUuid(uuid: String, phoneNumber: String) = firebaseHelper.setUuid(uuid, phoneNumber)

    fun getAllMemerFromServer(): MediatorLiveData<List<Member>> {
        val snapShotConverter = MediatorLiveData<List<Member>>()

        snapShotConverter.addSource(firebaseHelper.getAllMembers()) { allMemberSnapshot ->
            allMemberSnapshot?.let {
                val result = mutableListOf<Member>()
                for (snapshot in allMemberSnapshot.children) {
                    result.add(snapshot.getValue<Member>()!!)
                }
                snapShotConverter.postValue(result)
            }
        }
        return snapShotConverter
    }

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

    fun deleteAllMember() {
        launch { deleteAllMemberBG() }
    }

    private suspend fun deleteAllMemberBG() {
        withContext(Dispatchers.IO){
            memberDao?.deleteAll()
        }
    }
}