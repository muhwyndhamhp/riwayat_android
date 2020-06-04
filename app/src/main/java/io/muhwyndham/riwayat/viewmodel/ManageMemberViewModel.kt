package io.muhwyndham.riwayat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.muhwyndham.riwayat.model.Member
import io.muhwyndham.riwayat.repository.MemberRepository

class ManageMemberViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = MemberRepository(application)

    fun getMemberList() = repository.getAllMember()

    fun getSingleMember(phoneNumber: String) = repository.getMember(phoneNumber)

    fun insertMember(
        memberName : String,
        memberPhoneNumber : String
    ) : MutableLiveData<Boolean> {
        return if(checkIsValid(memberName, memberPhoneNumber)) {
            repository.setMember(Member(phoneNumber = memberPhoneNumber, memberName = memberName))
            MutableLiveData(true)
        } else {
            MutableLiveData(false)
        }
    }

    private fun checkIsValid(memberName: String, memberPhoneNumber: String) : Boolean{
        return (memberName.trim{ it <= ' '}.isNotEmpty()
                && memberPhoneNumber.trim{it <= ' '}.isNotEmpty())
    }

}