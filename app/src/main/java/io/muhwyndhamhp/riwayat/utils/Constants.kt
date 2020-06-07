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

        const val NOMOR_LP = "NOMOR_LP"
        const val CHILD_CHANGED = "CHILD_CHANGED"
        const val CHILD_ADDED = "CHILD_ADDED"
        const val CHILD_REMOVED = "CHILD_REMOVED"
    }
}