package io.muhwyndhamhp.riwayat.utils

import android.widget.Spinner
import kotlinx.android.synthetic.main.case_form_layout.*

fun parseLacCid(operator_spinner_1 : Spinner, toString: String): String {
    var lacCid = ""
    lacCid += when (operator_spinner_1.selectedItem) {
        "T.sel" -> "510-10"
        "Isat" -> "510-01"
        "XL/Axis" -> "510-11"
        "Tri" -> "510-89"
        "Smartfren" -> "510-09"
        else -> "000-00"
    }

    return "$lacCid-$toString"
}

fun findSelectedSpinnerPosition(lacCid: String) =
    when (lacCid.substring(startIndex = 0, endIndex = 6)) {
        "510-10" -> 0
        "510-01" -> 1
        "510-11" -> 2
        "510-89" -> 3
        "510-09" -> 4
        else -> 0
    }