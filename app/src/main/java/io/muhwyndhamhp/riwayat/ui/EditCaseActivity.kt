package io.muhwyndhamhp.riwayat.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.michaldrabik.classicmaterialtimepicker.CmtpDateDialogFragment
import com.michaldrabik.classicmaterialtimepicker.CmtpTimeDialogFragment
import com.michaldrabik.classicmaterialtimepicker.model.CmtpDate
import com.michaldrabik.classicmaterialtimepicker.utilities.setOnDatePickedListener
import com.michaldrabik.classicmaterialtimepicker.utilities.setOnTime24PickedListener
import com.opensooq.supernova.gligar.GligarPicker
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.utils.Constants
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_ADDRESS
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_LAT
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_LONG
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.LOCATION_NAME
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.NOMOR_LP
import io.muhwyndhamhp.riwayat.utils.Constants.Companion.RC_LOCATION_PICKER
import io.muhwyndhamhp.riwayat.utils.findSelectedSpinnerPosition
import io.muhwyndhamhp.riwayat.utils.parseLacCid
import io.muhwyndhamhp.riwayat.viewmodel.InputCaseViewModel
import kotlinx.android.synthetic.main.activity_edit_case.*
import kotlinx.android.synthetic.main.case_form_layout.*
import java.text.SimpleDateFormat
import java.util.*

class EditCaseActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var editCaseViewModel: InputCaseViewModel? = null

    private lateinit var operatorSpinnerList
            : MutableList<Spinner>

    private var imageLocalRefList: MutableList<String> = mutableListOf()
    private lateinit var ivList: List<ImageView>
    private lateinit var ibList: List<ImageButton>

    private lateinit var lacCidEditTextList: MutableList<EditText>
    private val mLacCidList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_case)

        editCaseViewModel = ViewModelProvider(this).get(InputCaseViewModel::class.java)

        operatorSpinnerList = mutableListOf(
            operator_spinner_1,
            operator_spinner_2,
            operator_spinner_3,
            operator_spinner_4,
            operator_spinner_5
        )

        lacCidEditTextList =
            mutableListOf(et_lac_cid_1, et_lac_cid_2, et_lac_cid_3, et_lac_cid_4, et_lac_cid_5)

        ivList = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6)
        ibList = listOf(ib_1, ib_2, ib_3, ib_4, ib_5, ib_6)

        fetchInitialUser()

        initiateSpinners()
        initiateSaksiAdditionButton()
        initiateDateTimePicker()
        initiateLocationPicker()

        initiateEditButtonListener()
        initiateImagePicker()
    }

    private fun fetchAndSetInitialData() {
        val nomorLp = intent.getStringExtra(NOMOR_LP)!!

        editCaseViewModel!!.getCase(nomorLp)!!.observe(this, Observer {
            if (it != null) {
                editCaseViewModel!!.currentCase.value = it
                inflateCaseToUI()
            }
        })

        editCaseViewModel!!.getCaseNotes(nomorLp)!!.observe(this, Observer {
            val currentCaseNotes = editCaseViewModel!!.currentCaseNotes.value!!
            for (caseNote in it) {
                currentCaseNotes[caseNote.timestamp.toString()] = caseNote
            }
            editCaseViewModel!!.currentCaseNotes.value = currentCaseNotes
        })
    }

    private fun inflateCaseToUI() {
        editCaseViewModel!!.getCase().observe(this, Observer { currentCase ->
            et_nomor_lp.setText(currentCase.nomorLP.replace("-", "/"))
            et_nama_pelapor.setText(currentCase.namaPelapor)
            et_hp_pelapor.setText(currentCase.hpPelapor)
            et_alamat_pelapor.setText(currentCase.alamatPelapor)
            et_waktu_kejadian.setText(parseTimestampFromString(currentCase.waktuKejadian))
            et_lokasi_kejadian.setText(currentCase.lokasiKejadian)
            if (currentCase.latLongKejadian.isNotEmpty())
                editCaseViewModel!!.latLong.value = LatLng(
                    currentCase.latLongKejadian.substringAfter("(").substringBefore(",").toDouble(),
                    currentCase.latLongKejadian.substringAfter(",").substringBefore(")").toDouble()
                )

            for (i in currentCase.lacCid.indices) {
                operatorSpinnerList[i].setSelection(findSelectedSpinnerPosition(currentCase.lacCid[i]))
                currentCase.lacCid[i].substring(startIndex = 6)
            }
            for (i in currentCase.lacCid.indices) {
                lacCidEditTextList[i].setText(currentCase.lacCid[i].substring(startIndex = 7))
                operatorSpinnerList[i].setSelection(findSelectedSpinnerPosition(currentCase.lacCid[i]))
            }
            pidana_spinner.setSelection(findSelectedPidanaSpinnerPosition())
            if (pidana_spinner.selectedItemPosition == 5) et_pidana_lain.setText(currentCase.tindakPidana)
            setDaftarSaksi()
            et_hasil_lidik.setText(currentCase.hasilLidik)
            imageLocalRefList.addAll(currentCase.imageUrls)
            setImageAsset()
        })

    }

    private fun setDaftarSaksi() {
        val daftarSaksi = editCaseViewModel!!.currentCase.value!!.daftarSaksi

        val saksiETLList = listOf(et_saksi_1, et_saksi_2, et_saksi_3, et_saksi_4, et_saksi_5)
        val saksiTILList = listOf(til_saksi_1, til_saksi_2, til_saksi_3, til_saksi_4, til_saksi_5)
        for (i in daftarSaksi.indices) {
            if (daftarSaksi[i].trim { it <= ' ' }.isNotEmpty()) {
                saksiETLList[i].setText(daftarSaksi[i])
                saksiTILList[i].visibility = View.VISIBLE
                if (i == 4) ib_add_saksi.visibility = View.GONE
                editCaseViewModel!!.saksiCounter.value = daftarSaksi.size
            }
        }
    }

    private fun findSelectedPidanaSpinnerPosition() =
        when (editCaseViewModel!!.currentCase.value!!.tindakPidana) {
            "Pembunuhan" -> 0
            "Curat" -> 1
            "Curas" -> 2
            "Penganiayaan" -> 3
            "Curanmor" -> 4
            else -> 5
        }

    @SuppressLint("SimpleDateFormat")
    private fun parseTimestampFromString(waktuKejadian: Long): String {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val date = Date(waktuKejadian)

        return format.format(date)
    }

    private fun initiateImagePicker() {
        bt_add_image.setOnClickListener {
            GligarPicker().requestCode(Constants.GLIGAR_PICKER).limit(6 - imageLocalRefList.size)
                .withActivity(this).show()
        }
    }

    private fun setImageAsset() {
        if (imageLocalRefList.size == 6) bt_add_image.visibility = View.INVISIBLE
        else bt_add_image.visibility = View.VISIBLE
        for (i in imageLocalRefList.indices) {
            Glide.with(this).load(imageLocalRefList[i])
                .into(ivList[i])
            ivList[i].visibility = View.VISIBLE
            ibList[i].visibility = View.VISIBLE
            ibList[i].setOnClickListener {
                imageLocalRefList.removeAt(i)
                ivList[imageLocalRefList.lastIndex + 1].visibility = View.GONE
                ibList[imageLocalRefList.lastIndex + 1].visibility = View.GONE
                setImageAsset()
            }
        }

    }

    private fun fetchInitialUser() {
        editCaseViewModel!!.getMember()!!.observe(this, Observer {
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

            val latLong = editCaseViewModel!!.latLong.value
            val latLongKejadian = latLong?.toString() ?: ""

            for (i in lacCidEditTextList.indices) {
                if (lacCidEditTextList[i].text.toString().trim { it <= ' ' }.isNotEmpty()) {
                    mLacCidList.add(
                        parseLacCid(
                            operatorSpinnerList[i],
                            lacCidEditTextList[i].text.toString()
                        )
                    )
                }
            }
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
                mLacCidList.isEmpty()
            ) {
                val toast = Toast.makeText(this, "Kolom tidak boleh kosong!", Toast.LENGTH_LONG)
                toast.show()
            } else {
                parent_scroll_view.fullScroll(ScrollView.FOCUS_UP)
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_upload.visibility = View.VISIBLE

                if (imageLocalRefList.isNotEmpty()) {
                    tv_process.text = "Mengupload gambar..."
                    tv_process.visibility = View.VISIBLE
                    editCaseViewModel!!.uploadImages(imageLocalRefList, this, this)
                        .observe(this, Observer { downloadURLs ->
                            tv_process.text = "Menambahkan catatan..."
                            tv_process.visibility = View.VISIBLE
                            val case = Case(
                                nomorLp,
                                namaPelapor,
                                nomorHpPelapor,
                                alamatPelapor,
                                waktuKejadian,
                                latLongKejadian,
                                lokasiKejadian,
                                mLacCidList,
                                tindakPidana,
                                daftarSaksi,
                                hasilLidik,
                                editCaseViewModel!!.currentMember.value!!.memberName,
                                System.currentTimeMillis(),
                                downloadURLs
                            )

                            editCaseViewModel!!.insertCase(case)
                            finish()

                        })
                } else {
                    tv_process.text = "Menambahkan catatan..."
                    tv_process.visibility = View.VISIBLE
                    val case = Case(
                        nomorLp,
                        namaPelapor,
                        nomorHpPelapor,
                        alamatPelapor,
                        waktuKejadian,
                        latLongKejadian,
                        lokasiKejadian,
                        mLacCidList,
                        tindakPidana,
                        daftarSaksi,
                        hasilLidik,
                        editCaseViewModel!!.currentMember.value!!.memberName,
                        System.currentTimeMillis()
                    )

                    editCaseViewModel!!.insertCase(case)
                    finish()
                }
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
                editCaseViewModel!!.latLong.value =
                    LatLng(locationLat.toDouble(), locationLong.toDouble())
            }
        }
        if (requestCode == Constants.GLIGAR_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                val imagesList = data?.extras!!.getStringArray(GligarPicker.IMAGES_RESULT)
                if (!imagesList.isNullOrEmpty()) {
                    imageLocalRefList.addAll(imagesList.toList())

                    if (imageLocalRefList.isNotEmpty()) {
                        setImageAsset()
                    }
                }
            }
        }
    }

    private fun initiateSaksiAdditionButton() {
        val saksiTILList = listOf(til_saksi_1, til_saksi_2, til_saksi_3, til_saksi_4, til_saksi_5)
        ib_add_saksi.setOnClickListener {
            if (editCaseViewModel!!.saksiCounter.value!! < 5) {
                saksiTILList[editCaseViewModel!!.saksiCounter.value!!].visibility = View.VISIBLE
                editCaseViewModel!!.saksiCounter.value =
                    editCaseViewModel!!.saksiCounter.value!! + 1
                if (editCaseViewModel!!.saksiCounter.value!! >= 5) ib_add_saksi.visibility =
                    View.GONE
            }
        }
    }

    private fun initiateSpinners() {

        /**
         * Spinner for Operator's MIC
         */

        for (spinner in operatorSpinnerList) {
            ArrayAdapter.createFromResource(
                this,
                R.array.operator_list,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
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
