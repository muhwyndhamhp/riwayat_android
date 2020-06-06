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
        const val LOCATION_LATLONG = "LOCATION_LAT_LONG"
        const val LOCATION_NAME = "LOCATION_NAME"
    }
}