package io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.view

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import io.golos.cyber_android.R
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executors
import com.google.api.services.drive.model.File as GoogleDriveFile

class KeysToGoogleDriveExporter(private val fragment: Fragment) {
    companion object {
        private const val SIGN_IN_REQUEST = 400
    }

    enum class ExportState {
        STARTED,
        SUCCESS,
        FAIL
    }

    private var exportStateListener: ((ExportState) -> Unit)? = null

    private lateinit var fileToUpload: File

    fun setOnExportStateListener(listener: ((ExportState) -> Unit)?) {
        exportStateListener = listener
    }

    fun startExport(fileToUpload: File) {
        this.fileToUpload = fileToUpload
        startSignIn()
    }

    private fun startSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()

        val client = GoogleSignIn.getClient(fragment.requireContext(), signInOptions)
        fragment.startActivityForResult(client.signInIntent, SIGN_IN_REQUEST)
    }

    fun processSignInResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("UPLOAD", "processSignInResult(requestCode: $requestCode; resultCode: $resultCode)")
        if(requestCode == SIGN_IN_REQUEST && resultCode == Activity.RESULT_OK) {
            loginToGoogleDrive(data!!)
        }
    }

    private fun loginToGoogleDrive(data: Intent) {
        Log.d("UPLOAD", "loginToGoogleDrive")

        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener {
                Log.d("UPLOAD", "success")
                val credentials = GoogleAccountCredential.usingOAuth2(fragment.requireContext(), listOf(DriveScopes.DRIVE_FILE))
                credentials.selectedAccount = it.account

                val driveService = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credentials)
                    .setApplicationName(fragment.requireContext().getString(R.string.app_name))
                    .build()

                Log.d("UPLOAD", "started")
                exportStateListener?.invoke(ExportState.STARTED)

                Executors.newSingleThreadExecutor().execute {
                    uploadFile(driveService)
                }
            }
            .addOnFailureListener {
                Log.d("UPLOAD", "fail")
                Timber.e(it)
            }
    }

    private fun uploadFile(driveService: Drive) {
        try {
            Log.d("UPLOAD", "start upload")
            val metadata = GoogleDriveFile()
            metadata.name = fileToUpload.name

            val content = FileContent("application/pdf", fileToUpload)

            // Upload
            val uploadState = driveService
                .files()
                .create(metadata, content)
                .execute()
                ?.let { ExportState.SUCCESS }
                ?: ExportState.FAIL

            Log.d("UPLOAD", "upload completed: $uploadState")

            Handler(Looper.getMainLooper()).post { exportStateListener?.invoke(uploadState) }
        } catch (ex: Exception) {
            Timber.e(ex)
            Handler(Looper.getMainLooper()).post { exportStateListener?.invoke(ExportState.FAIL) }
        }
    }
}