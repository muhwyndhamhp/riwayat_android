package io.muhwyndhamhp.riwayat.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.michaldrabik.classicmaterialtimepicker.CmtpDateDialogFragment
import com.michaldrabik.classicmaterialtimepicker.CmtpTimeDialogFragment
import com.michaldrabik.classicmaterialtimepicker.model.CmtpDate
import com.michaldrabik.classicmaterialtimepicker.utilities.setOnDatePickedListener
import com.michaldrabik.classicmaterialtimepicker.utilities.setOnTime24PickedListener
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_ADDRESS
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_LAT
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_LONG
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_NAME
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.RC_LOCATION_PICKER
import io.muhwyndhamhp.riwayat.viewmodel.InputCaseViewModel
import kotlinx.android.synthetic.main.case_form_layout.*

class InputCaseActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var saksiCounter = 1
    lateinit var latLong: LatLng

    private var inputCaseViewModel: InputCaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_case)

        inputCaseViewModel = ViewModelProvider(this).get(InputCaseViewModel::class.java)

        initiateSpinners()
        initiateSaksiAdditionButton()
        initiateDateTimePicker()
        initiateLocationPicker()

        initiateInputButtonListener()
    }

    /**
     * Below are Data specific functions, for controlling data flow and communicating with ViewModel
     */

    private fun initiateInputButtonListener() {

    }

    /**
     * below are UI specific functions, for controlling UI behaviour
     */

    private fun initiateLocationPicker() {
        ib_select_location.setOnClickListener {
            startActivityForResult(
                Intent(this, LocationPickerActivity::class.java),
                RC_LOCATION_PICKER
            )
        }
    }

    private fun initiateDateTimePicker() {
        et_waktu_kejadian.setOnClickListener { initiateDatePicker() }
    }

    private fun initiateDatePicker() {
        val datePicker = CmtpDateDialogFragment.newInstance()
        datePicker.setInitialDate(day = 2, month = 1, year = 2019)
        datePicker.setOnDatePickedListener { date ->
            initiateTimePicker(date)
        }
        datePicker.show(supportFragmentManager, "Tag")
    }

    private fun initiateTimePicker(date: CmtpDate) {
        val timePicker = CmtpTimeDialogFragment.newInstance()

        timePicker.setInitialTime24(23, 30)
        timePicker.setOnTime24PickedListener { time ->
            et_waktu_kejadian.setText("${date.toString()} ${time.toString()}")
        }
        timePicker.show(supportFragmentManager, "Tag")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 5) til_pidana_lain.visibility = View.VISIBLE
        else til_pidana_lain.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_LOCATION_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                val locationName = data!!.getStringExtra(LOCATION_NAME)
                val locationLat = data.getStringExtra(LOCATION_LAT)!!
                val locationLong = data.getStringExtra(LOCATION_LONG)!!
                val locationAddress = data.getStringExtra(LOCATION_ADDRESS)

                et_lokasi_kejadian.setText(if (locationName != null && locationName.length < 5) locationAddress else "$locationName, $locationAddress")
                latLong = LatLng(locationLat.toDouble(), locationLong.toDouble())
            }
        }
    }

    private fun initiateSaksiAdditionButton() {
        val saksiTILList = listOf(til_saksi_1, til_saksi_2, til_saksi_3, til_saksi_4, til_saksi_5)
        ib_add_saksi.setOnClickListener {
            if (saksiCounter < 5) {
                saksiTILList[saksiCounter].visibility = View.VISIBLE
                saksiCounter++
                if (saksiCounter >= 5) ib_add_saksi.visibility = View.GONE
            }
        }
    }

    private fun initiateSpinners() {

        /**
         * Spinner for Operator's MIC
         */
        ArrayAdapter.createFromResource(
            this,
            R.array.operator_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            operator_spinner.adapter = adapter
        }

        /**
         * Spinner for Tindakan Kriminal
         */
        ArrayAdapter.createFromResource(
            this,
            R.array.pidana_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            pidana_spinner.adapter = adapter
        }

        //add listener to item selection in Tindakan Kriminal Spinner for item 'Lain-lain'
        pidana_spinner.onItemSelectedListener = this
    }

}
