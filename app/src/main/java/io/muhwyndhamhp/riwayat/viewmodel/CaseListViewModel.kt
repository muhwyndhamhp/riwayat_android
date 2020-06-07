package io.muhwyndhamhp.riwayat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.repository.AppRepository

class CaseListViewModel(application: Application) : AndroidViewModel(application) {


    private var repository = AppRepository(application)
    private var mediatorLiveData = MediatorLiveData<List<Case>>()

    fun getAllCase() :MediatorLiveData<List<Case>>{

        mediatorLiveData.addSource(repository.getAllCase()!!){
            mediatorLiveData.postValue(it)
        }
        mediatorLiveData.addSource(repository.getAllCaseFromServer()){
            mediatorLiveData.postValue(it)
        }
        return mediatorLiveData
    }


    fun deleteCase(case: Case) = repository.deleteCase(case)


}