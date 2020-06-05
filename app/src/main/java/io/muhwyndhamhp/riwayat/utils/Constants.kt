package io.muhwyndhamhp.riwayat.utils

class Constants {
    companion object{
        const val RC_SIGN_IN = 101
        enum class FirebaseUploadStatus {
            FAILED,
            COMPLETED,
            WAITING,
            WRONG_INPUT,
            CONFLICT
        }
    }
}