package io.muhwyndhamhp.riwayat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.repository.MemberRepository
import io.muhwyndhamhp.riwayat.utils.Constants

class ManageMemberViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = MemberRepository(application)
    private lateinit var memberList: LiveData<MutableList<Member>>

    fun getMemberList(): LiveData<MutableList<Member>> {
        memberList = repository.getAllMember()!!
        return memberList
    }

    fun getSingleMember(phoneNumber: String) = repository.getMember(phoneNumber)

    fun insertMember(
        memberName: String,
        memberPhoneNumber: String
    ): MutableLiveData<Constants.Companion.FirebaseUploadStatus> {
        return if (checkIsValid(memberName, memberPhoneNumber)) {
            if (checkIfConflict(memberPhoneNumber)) {
                repository.setMember(
                    Member(
                        phoneNumber = memberPhoneNumber,
                        memberName = memberName
                    )
                )
            } else {
                MutableLiveData(Constants.Companion.FirebaseUploadStatus.CONFLICT)
            }
        } else {
            MutableLiveData(Constants.Companion.FirebaseUploadStatus.WRONG_INPUT)
        }
    }

    private fun checkIsValid(memberName: String, memberPhoneNumber: String): Boolean {
        return (memberName.trim { it <= ' ' }.isNotEmpty()
                && memberPhoneNumber.trim { it <= ' ' }.isNotEmpty())
    }

    private fun checkIfConflict(memberPhoneNumber: String): Boolean {
        if (memberList.value!!.size > 0) {
            for (member in memberList.value!!) {
                if (member.phoneNumber == memberPhoneNumber) return false
            }
        }
        return true
    }

    fun deleteMember(member: Member): MutableLiveData<Constants.Companion.FirebaseUploadStatus> =
        repository.deleteMember(member)

}