package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.obsez.android.lib.filechooser.ChooserDialog

class KeysToLocalDriveExporter(private val fragment: Fragment) {
    companion object {
        private const val PERMISSION_REQUEST = 100
    }

    private var onExportPathSelectedListener: ((String) -> Unit)? = null
    private var onExportErrorListener: (() -> Unit)? = null

    fun setOnExportPathSelectedListener(listener: ((String) -> Unit)?) {
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
                onExportPathSelectedListener?.invoke(path)
            }
            .build()
            .show()
    }
}