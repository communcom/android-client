package io.golos.cyber_android.ui.shared.keys_to_pdf

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.obsez.android.lib.filechooser.ChooserDialog
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.UserKey
import timber.log.Timber
import java.io.File

class PdfKeysExporter(private val fragment: Fragment) {
    companion object {
        private const val PERMISSION_REQUEST = 100
        private const val VIEW_PDF_REQUEST = 101
    }

    private var onExportCompletedListener: (() -> Unit)? = null
    private var onExportPathSelectedListener: (() -> Unit)? = null
    private var onExportErrorListener: (() -> Unit)? = null

    private lateinit var selectedPath:String

    fun setOnExportCompletedListener(listener: (() -> Unit)?) {
        this.onExportCompletedListener = listener
    }

    fun setOnExportPathSelectedListener(listener: (() -> Unit)?) {
        this.onExportPathSelectedListener = listener
    }

    fun setOnExportErrorListener(listener: (() -> Unit)?) {
        this.onExportErrorListener = listener
    }

    fun startExport() {
        if (!isPermissionsGranted(fragment)) {
            fragment.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST)
        } else {
            showSaveDialog()
        }
    }

    fun processDataToExport(userName: String, userId: UserIdDomain, keys: List<UserKey>) {
        val keysSummary = PdfKeysUtils.getKeysSummary(fragment.requireContext(), userName, userId.userId, keys)
        val saveResult = PdfKeysUtils.saveTextAsPdfDocument(keysSummary, selectedPath)

        if (saveResult) {
            onSaveSuccess(PdfKeysUtils.getKeysSavePathInDir(selectedPath))
        } else {
            onExportErrorListener?.invoke()
        }
    }

    fun processViewPdfResult(requestCode: Int) {
        if (requestCode == VIEW_PDF_REQUEST) {
            onExportCompletedListener?.invoke()
        }
    }

    fun processRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                showSaveDialog()
            else {
                onExportErrorListener?.invoke()
            }
        }
    }

    private fun isPermissionsGranted(fragment: Fragment) =
        ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun showSaveDialog() {
        ChooserDialog(fragment.requireActivity())
            .withFilter(true, false)
            .displayPath(false)
            .withChosenListener { path, _ ->
                selectedPath = path
                onExportPathSelectedListener?.invoke()
            }
            .build()
            .show()
    }

    private fun onSaveSuccess(keysFile: String) {
        val dialog = NotificationDialog.newInstance(fragment.resources.getString(R.string.keys_saved_successfully))

        dialog.listener = {
            val intent = Intent(Intent.ACTION_VIEW)
            val mimeType = "application/pdf"
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                val fileURI = FileProvider.getUriForFile(
                    fragment.requireContext(),
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    File(keysFile)
                )
                intent.setDataAndType(fileURI, mimeType)
            } else {
                intent.setDataAndType(Uri.fromFile(File(keysFile)), mimeType)
            }

            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION

            try {
                fragment.startActivityForResult(intent, VIEW_PDF_REQUEST)
            } catch (e: ActivityNotFoundException) {
                Timber.e(e)
                onExportCompletedListener?.invoke()
            }
        }

        dialog.isCancelable = false
        dialog.show(fragment.requireFragmentManager(), "notification")
    }
}