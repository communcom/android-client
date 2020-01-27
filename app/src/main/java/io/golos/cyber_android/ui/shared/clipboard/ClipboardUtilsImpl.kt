package io.golos.cyber_android.ui.shared.clipboard

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import javax.inject.Inject

class ClipboardUtilsImpl
@Inject
constructor(
    private val appContext: Context
) : ClipboardUtils {
    override fun putPlainText(value: String) {
        val manager = getManager()
        manager.primaryClip = ClipData.newPlainText(value, value)
    }

    override fun getPlainText(): String? {
        val manager = getManager()

        if(!manager.hasPrimaryClip()) {
            return null
        }

        return if(manager.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == true) {
            manager.primaryClip?.getItemAt(0)?.text?.toString()
        } else {
            null
        }
    }

    private fun getManager() = appContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
}