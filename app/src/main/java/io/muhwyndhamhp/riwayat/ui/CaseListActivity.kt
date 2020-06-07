package io.muhwyndhamhp.riwayat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.adapter.CaseListRVAdapter
import io.muhwyndhamhp.riwayat.viewmodel.CaseListViewModel
import kotlinx.android.synthetic.main.activity_case_list.*

class CaseListActivity : AppCompatActivity() {

    var caseListViewModel : CaseListViewModel? = null

    private lateinit var adapter : CaseListRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_list)

        caseListViewModel = ViewModelProvider(this).get(CaseListViewModel::class.java)

        fetchInitialData()
    }

    private fun fetchInitialData() {
        caseListViewModel?.getAllCase()!!.observe(this, Observer { caseList ->
            adapter = CaseListRVAdapter(this, caseList)
            rv_case_list.adapter = adapter
            rv_case_list.layoutManager = LinearLayoutManager(this)
        })
    }
}
