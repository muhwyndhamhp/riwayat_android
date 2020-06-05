package io.muhwyndhamhp.riwayat.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import io.muhwyndhamhp.riwayat.R
import kotlinx.android.synthetic.main.case_form_layout.*

class InputCaseActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var saksiCounter = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_case)

        ArrayAdapter.createFromResource(
            this,
            R.array.operator_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            operator_spinner.adapter = adapter

        }
        ArrayAdapter.createFromResource(
            this,
            R.array.pidana_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            pidana_spinner.adapter = adapter
        }
        pidana_spinner.onItemSelectedListener = this

        val saksiTILList = listOf(til_saksi_1, til_saksi_2, til_saksi_3, til_saksi_4, til_saksi_5)
        ib_add_saksi.setOnClickListener {
            if (saksiCounter < 5){
                saksiTILList[saksiCounter].visibility = View.VISIBLE
                saksiCounter++
                if(saksiCounter >= 5) ib_add_saksi.visibility = View.GONE
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 5) til_pidana_lain.visibility = View.VISIBLE
        else til_pidana_lain.visibility = View.GONE
    }

}
