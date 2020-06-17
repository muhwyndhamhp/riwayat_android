package io.muhwyndhamhp.riwayat.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import io.muhwyndhamhp.riwayat.R
import io.muhwyndhamhp.riwayat.adapter.HomeCaseNoteAdapter
import io.muhwyndhamhp.riwayat.model.Case
import io.muhwyndhamhp.riwayat.repository.AppRepository
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*
import kotlin.math.roundToInt


class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: HomeCaseNoteAdapter
    var timer = Timer()
    private val handler = Handler()
    private var position = 0
    private var arraySize = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        Glide.with(this).load(R.raw.nightcity).into(iv_background)

        getCurrentUser()
        cv_anggota.setOnClickListener {
            startActivity(
                Intent(
                    this@HomeActivity,
                    ManageMemberActivity::class.java
                )
            )
        }

        cv_daftar_kasus.setOnClickListener {
            startActivity(Intent(this@HomeActivity, CaseListActivity::class.java))
        }

        cv_input_kasus.setOnClickListener {
            startActivity(
                Intent(
                    this@HomeActivity,
                    InputCaseActivity::class.java
                )
            )
        }

        initiateLatestCases()

    }

    private fun initiateLatestCases() {
        AppRepository(application).getNewestCases()?.observe(this, Observer {
            initiateRV(it)
        })
    }

    private fun initiateRV(caseList: List<Case>) {
        arraySize = caseList.size
        adapter = HomeCaseNoteAdapter(this, caseList)
        rv_interactive_newest.adapter = adapter
        rv_interactive_newest.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_interactive_newest.onFlingListener = null;
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv_interactive_newest)
        attachTimer()
    }

    private fun attachTimer() {
        timer.cancel()
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    if (position == arraySize) position = 0
                    rv_interactive_newest.smoothScrollToPosition(position)
                    position++
                }
            }

        }, 5000, 5000)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    private fun getCurrentUser() {
        val currentUserPhoneNumber =
            FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.replace("+62", "0")
        AppRepository(this.application).getMember(currentUserPhoneNumber)!!.observe(this, Observer {
            text_current_user.text = "Selamat datang,\n${it.memberName}!"
            if (it.isAdmin) cv_anggota.visibility = View.VISIBLE
            else {
                cv_anggota.visibility = View.GONE

                val constraintSet = ConstraintSet()
                constraintSet.clone(cv_parent)
                constraintSet.connect(
                    cv_input_kasus.id,
                    ConstraintSet.START,
                    bt_input.id,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    cv_input_kasus.id,
                    ConstraintSet.BOTTOM,
                    bt_input.id,
                    ConstraintSet.TOP
                )
                constraintSet.connect(
                    cv_daftar_kasus.id,
                    ConstraintSet.END,
                    bt_input.id,
                    ConstraintSet.END
                )
                constraintSet.connect(
                    cv_daftar_kasus.id,
                    ConstraintSet.BOTTOM,
                    bt_input.id,
                    ConstraintSet.TOP
                )
                constraintSet.setMargin(cv_input_kasus.id, ConstraintSet.START, pxFromDips(20))
                constraintSet.setMargin(cv_daftar_kasus.id, ConstraintSet.END, pxFromDips(20))
                constraintSet.setMargin(cv_daftar_kasus.id, ConstraintSet.BOTTOM, pxFromDips(40))
                constraintSet.setMargin(cv_input_kasus.id, ConstraintSet.BOTTOM, pxFromDips(40))

                constraintSet.constrainHeight(bt_input.id, pxFromDips(80))

                constraintSet.applyTo(cv_parent)

                bt_input.setBackgroundColor(resources.getColor(R.color.colorAccent))
                bt_input.setTextColor(resources.getColor(R.color.white))
            }

        })
    }

    private fun pxFromDips(dips: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dips.toFloat(), resources.displayMetrics
    ).roundToInt()
}
