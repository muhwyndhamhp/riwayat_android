package io.muhwyndhamhp.riwayat.utils

class Constants {
    companion object {
        const val RC_SIGN_IN = 101

        enum class FirebaseUploadStatus {
            FAILED,
            COMPLETED,
            WAITING,
            WRONG_INPUT,
            CONFLICT
        }

        const val LOCATION_ADDRESS = "LOCATION_ADDRESS"
        const val LOCATION_LAT= "LOCATION_LAT"
        const val LOCATION_LONG = "LOCATION_LONG"
        const val LOCATION_NAME = "LOCATION_NAME"
        const val RC_LOCATION_PICKER = 20002
    }
}