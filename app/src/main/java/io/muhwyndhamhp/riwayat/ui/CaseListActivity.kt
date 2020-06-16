package io.muhwyndhamhp.riwayat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
        attaachSearchListener()
    }

    private fun attaachSearchListener() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                initiateSearch()
            }

        })

        bt_search.setOnClickListener { initiateSearch() }
    }

    private fun initiateSearch() {
        if (et_search.text.toString().trim { it <= ' ' }.isNotEmpty()) {
            caseListViewModel?.getCasesThatContainsStrings(
                "%${et_search.text.toString().trim()}%"
            )!!.observe(this@CaseListActivity, Observer {
                renderRV(it)
            })
        } else if (et_search.text.toString().trim { it <= ' ' }.isEmpty()) {
            renderCaseList()
        }
    }

    private fun renderCaseList() {
        caseListViewModel!!.getAllCase().observe(this, Observer {
            renderRV(it)
        })
    }

    private fun renderRV(caseList: List<Case>) {
        adapter = CaseListRVAdapter(this, caseList as MutableList<Case>)
        rv_case_list.adapter = adapter
        rv_case_list.layoutManager = LinearLayoutManager(this)
    }
}
