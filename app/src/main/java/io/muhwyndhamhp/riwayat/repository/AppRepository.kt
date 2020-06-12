package io.muhwyndhamhp.riwayat.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.ktx.getValue
import io.muhwyndhamhp.riwayat.dao.CaseDao
import io.muhwyndhamhp.riwayat.dao.CaseNoteDao
import io.muhwyndhamhp.riwayat.dao.MemberDao
import io.muhwyndhamhp.riwayat.database.RoomDatabase
import io.muhwyndhamhp.riwayat.helper.FirebaseHelperImplementation
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.CaseNote
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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
     * Below are functions for CaseNote Management
     */


    fun uploadCaseNote(caseNote: CaseNote, isWithServer: Boolean) {
        launch { uploadCaseNoteBG(caseNote, isWithServer) }
    }

    private suspend fun uploadCaseNoteBG(caseNote: CaseNote, isWithServer: Boolean) {
        withContext(Dispatchers.IO) {
            caseNoteDao!!.insertCaseNote(caseNote)
            if (isWithServer) {
                firebaseHelper.uploadCaseNote(caseNote)
            }
        }
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
                snapShotConverter.postValue(result.asReversed())
                refreshAllCase(result)
            }
        }
        return snapShotConverter
    }

//    fun subscribeCaseToServer(): MediatorLiveData<Pair<String, Case>> {
//        val snapshotConverter = MediatorLiveData<Pair<String, Case>>()
//
//        snapshotConverter.addSource(getAllCase()!!) {
//            for (case in it) {
//                snapshotConverter.postValue(Pair(CHILD_ADDED, case))
//            }
//
//            snapshotConverter.removeSource(getAllCase()!!)
//        }
//
//        snapshotConverter.addSource(firebaseHelper.getCaseChildListener()) {
//            when (it!!.first) {
//                CHILD_ADDED, CHILD_CHANGED -> insertCase(it.second.getValue<Case>()!!, false)
//                CHILD_REMOVED -> deleteCase(it.second.getValue<Case>()!!)
//            }
//            snapshotConverter.postValue(Pair(it!!.first, it.second.getValue<Case>()!!))
//        }
//        return snapshotConverter
//    }

    fun insertCase(case: Case, isWithServer: Boolean) {
        launch { insertCaseBG(case, isWithServer) }
    }

    fun deleteCase(case: Case) {
        launch { deleteCaseBG(case) }
    }

    fun deleteAllCase() {
        launch { deleteAllCaseBG() }
    }

    fun refreshAllCase(caseList: List<Case>): MutableLiveData<Boolean> {
        val isRefreshed = MutableLiveData(false)
        launch { refreshAllCaseBG(caseList, isRefreshed) }
        return isRefreshed
    }

    private suspend fun refreshAllCaseBG(
        caseList: List<Case>,
        refreshed: MutableLiveData<Boolean>
    ) {
        withContext(Dispatchers.IO) {
            val a = caseDao?.deleteAll()
            for (case in caseList) {
                caseDao?.insertCase(case)

                if (case.caseNotes.isNotEmpty()) {
                    val caseNotes: List<CaseNote> = case.caseNotes.values.toList()
                    for (caseNote in caseNotes) {
                        caseNoteDao?.insertCaseNote(caseNote)

                    }
                }
            }
            refreshed.postValue(true)
        }

    }

    private suspend fun deleteAllCaseBG() {
        withContext(Dispatchers.IO) {
            val a = caseDao?.deleteAll()
            Log.d("CASE_DAO", a.toString())
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
        if (isWithServer)
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
        withContext(Dispatchers.IO) {
            memberDao?.deleteAll()
        }
    }

    fun uploadImages(compressedImages: MutableList<File>) =
        firebaseHelper.uploadImage(compressedImages)
}