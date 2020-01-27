package io.golos.cyber_android.ui.shared.clipboard

interface ClipboardUtils {
    fun putPlainText(value: String)
    fun getPlainText(): String?
}