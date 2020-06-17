package io.muhwyndhamhp.riwayat.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.michaldrabik.classicmaterialtimepicker.CmtpDateDialogFragment
import com.michaldrabik.classicmaterialtimepicker.CmtpTimeDialogFragment
import com.michaldrabik.classicmaterialtimepicker.model.CmtpDate
import com.michaldrabik.classicmaterialtimepicker.utilities.setOnDatePickedListener
import com.michaldrabik.classicmaterialtimepicker.utilities.setOnTime24PickedListener
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.model.CaseNote
import io.muhwyndhamhp.riwayat.model.Member
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_ADDRESS
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_LAT
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_LONG
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_NAME
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.NOMOR_LP
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.RC_LOCATION_PICKER
import io.muhwyndhamhp.riwayat.viewmodel.InputCaseViewModel
import kotlinx.android.synthetic.main.activity_edit_case.*
import kotlinx.android.synthetic.main.case_form_layout.*
import java.text.SimpleDateFormat
import java.util.*

class EditCaseActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var saksiCounter = 1
    private lateinit var latLong: LatLng
    private lateinit var currentMember: Member
    private lateinit var currentCase: Case
    private var currentCaseNotes = mutableMapOf<String, CaseNote>()

    private var editCaseViewModel: InputCaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_case)

        editCaseViewModel = ViewModelProvider(this).get(InputCaseViewModel::class.java)

        fetchInitialUser()

        initiateSpinners()
        initiateSaksiAdditionButton()
        initiateDateTimePicker()
        initiateLocationPicker()

        initiateEditButtonListener()
    }

    private fun fetchAndSetInitialData() {
        val nomorLp = intent.getStringExtra(NOMOR_LP)!!

        editCaseViewModel!!.getCase(nomorLp)!!.observe(this, Observer {
            if (it != null) {
                currentCase = it
                inflateCaseToUI()
            }
        })

        editCaseViewModel!!.getCaseNotes(nomorLp)!!.observe(this, Observer {
            for(caseNote in it){
                currentCaseNotes[caseNote.timestamp.toString()] = caseNote
            }
        })
    }

    private fun inflateCaseToUI() {
        et_nomor_lp.setText(currentCase.nomorLP.replace("-", "/"))
        et_nama_pelapor.setText(currentCase.namaPelapor)
        et_hp_pelapor.setText(currentCase.hpPelapor)
        et_alamat_pelapor.setText(currentCase.alamatPelapor)
        et_waktu_kejadian.setText(parseTimestampFromString(currentCase.waktuKejadian))
        et_lokasi_kejadian.setText(currentCase.lokasiKejadian)
        latLong = LatLng(
            currentCase.latLongKejadian.substringAfter("(").substringBefore(",").toDouble(),
            currentCase.latLongKejadian.substringAfter(",").substringBefore(")").toDouble()
        )
        operator_spinner.setSelection(findSelectedSpinnerPosition())
        et_lac_cid.setText(currentCase.lacCid.substringAfter("-"))
        pidana_spinner.setSelection(findSelectedPidanaSpinnerPosition())
        if (pidana_spinner.selectedItemPosition == 5) et_pidana_lain.setText(currentCase.tindakPidana)
        setDaftarSaksi()
        et_hasil_lidik.setText(currentCase.hasilLidik)
    }

    private fun setDaftarSaksi() {
        val daftarSaksi = currentCase.daftarSaksi

        val saksiETLList = listOf(et_saksi_1, et_saksi_2, et_saksi_3, et_saksi_4, et_saksi_5)
        val saksiTILList = listOf(til_saksi_1, til_saksi_2, til_saksi_3, til_saksi_4, til_saksi_5)
        for (i in daftarSaksi.indices) {
            if (daftarSaksi[i].trim { it <= ' ' }.isNotEmpty()) {
                saksiETLList[i].setText(daftarSaksi[i])
                saksiTILList[i].visibility = View.VISIBLE
                if (i == 4) ib_add_saksi.visibility = View.GONE
                saksiCounter = daftarSaksi.size
            }
        }
    }

    private fun findSelectedPidanaSpinnerPosition() = when (currentCase.tindakPidana) {
        "Pembunuhan" -> 0
        "Curat" -> 1
        "Curas" -> 2
        "Penganiayaan" -> 3
        "Curanmor" -> 4
        else -> 5
    }


    private fun findSelectedSpinnerPosition() = when (currentCase.lacCid.substringBefore("-")) {
        "51010" -> 0
        "51001" -> 1
        "51011" -> 2
        "51089" -> 3
        "51009" -> 4
        else -> 0
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseTimestampFromString(waktuKejadian: Long): String {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val date = Date(waktuKejadian)

        return format.format(date)
    }

    private fun fetchInitialUser() {
        editCaseViewModel!!.getCurrentMember()!!.observe(this, Observer {
            currentMember = it
            fetchAndSetInitialData()
        })
    }

    /**
     * Below are Data specific functions, for controlling data flow and communicating with ViewModel
     */
    // TODO THIS IS TEMPORARY! Please fix for more readable code here later
    private fun initiateEditButtonListener() {
        bt_edit_case.setOnClickListener {
            val nomorLp = if (et_nomor_lp.text.toString().trim { it <= ' ' }
                    .isNotEmpty()) et_nomor_lp.text.toString().replace("/", "-") else null

            val namaPelapor = if (et_nama_pelapor.text.toString().trim { it <= ' ' }
                    .isNotEmpty()) et_nama_pelapor.text.toString() else null

            val nomorHpPelapor = if (et_hp_pelapor.text.toString().trim { it <= ' ' }
                    .isNotEmpty()) et_hp_pelapor.text.toString() else null

            val alamatPelapor = if (et_alamat_pelapor.text.toString().trim { it <= ' ' }
                    .isNotEmpty()) et_alamat_pelapor.text.toString() else null

            val waktuKejadian = if (et_waktu_kejadian.text.toString().trim { it <= ' ' }
                    .isNotEmpty()) parseTimestamp(et_waktu_kejadian.text.toString()) else null

            val lokasiKejadian = if (et_lokasi_kejadian.text.toString().trim { it <= ' ' }
                    .isNotEmpty()) et_lokasi_kejadian.text.toString() else null

            val latLongKejadian = if (::latLong.isInitialized) latLong.toString() else ""

            val lacCid =
                if (et_lac_cid.text.toString().trim { it <= ' ' }.isNotEmpty()) parseLacCid(
                    et_lac_cid.text.toString()
                ) else null

            val tindakPidana = parseTindakPidana(et_pidana_lain.text.toString())

            val daftarSaksi = getDaftarSaksi()

            val hasilLidik = if (et_hasil_lidik.text.toString().trim { it <= ' ' }
                    .isNotEmpty()) et_hasil_lidik.text.toString() else ""


            if (
                nomorLp == null ||
                namaPelapor == null ||
                nomorHpPelapor == null ||
                alamatPelapor == null ||
                waktuKejadian == null ||
                lokasiKejadian == null ||
                lacCid == null
            ) {
                val toast = Toast.makeText(this, "Kolom tidak boleh kosong!", Toast.LENGTH_LONG)
                toast.show()
            } else {
                val case = Case(
                    nomorLp,
                    namaPelapor,
                    nomorHpPelapor,
                    alamatPelapor,
                    waktuKejadian,
                    latLongKejadian,
                    lokasiKejadian,
                    lacCid,
                    tindakPidana,
                    daftarSaksi,
                    hasilLidik,
                    currentMember.memberName,
                    System.currentTimeMillis(),
                    currentCaseNotes
                )

                editCaseViewModel!!.insertCase(case)
                finish()
            }
        }
    }

    private fun getDaftarSaksi(): List<String> {
        val daftarSaksi = mutableListOf<String>()
        val saksiETLList = listOf(et_saksi_1, et_saksi_2, et_saksi_3, et_saksi_4, et_saksi_5)
        for (saksiEt in saksiETLList) {
            if (saksiEt.text.toString().trim { it <= ' ' }
                    .isNotEmpty()) daftarSaksi.add(saksiEt.text.toString())
        }

        return daftarSaksi
    }

    private fun parseTindakPidana(toString: String): String {
        return if (pidana_spinner.selectedItemPosition != 5) pidana_spinner.selectedItem.toString() else toString
    }

    private fun parseLacCid(toString: String): String {
        var lacCid = ""
        lacCid += when (operator_spinner.selectedItem) {
            "T.sel" -> "51010"
            "Isat" -> "51001"
            "Xl/Axis" -> "51011"
            "Tri" -> "51089"
            "Smartfren" -> "51009"
            else -> "00000"
        }

        return "$lacCid-$toString"
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseTimestamp(toString: String): Long {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val date = format.parse(toString)!!

        return date.time
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

    @SuppressLint("SetTextI18n")
    private fun initiateTimePicker(date: CmtpDate) {
        val timePicker = CmtpTimeDialogFragment.newInstance()

        timePicker.setInitialTime24(23, 30)
        timePicker.setOnTime24PickedListener { time ->
            et_waktu_kejadian.setText("$date $time")
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
