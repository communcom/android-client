package io.golos.cyber_android.core.clipboard

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