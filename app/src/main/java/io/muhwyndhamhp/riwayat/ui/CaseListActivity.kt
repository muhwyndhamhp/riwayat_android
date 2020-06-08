package io.muhwyndhamhp.riwayat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.adapter.CaseListRVAdapter
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.viewmodel.CaseListViewModel
import kotlinx.android.synthetic.main.activity_case_list.*

class CaseListActivity : AppCompatActivity() {

    var caseListViewModel: CaseListViewModel? = null

    private lateinit var adapter: CaseListRVAdapter
    private val caseList = mutableListOf<Case>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_list)

//        Glide.with(this).load(R.raw.smart_city).into(cl_background)
        caseListViewModel = ViewModelProvider(this).get(CaseListViewModel::class.java)

        renderCaseList()
    }

    private fun renderCaseList() {
        caseListViewModel!!.getAllCase().observe(this, Observer {
            adapter = CaseListRVAdapter(this, it as MutableList<Case>)
            rv_case_list.adapter = adapter
            rv_case_list.layoutManager = LinearLayoutManager(this)
        })
    }
}
