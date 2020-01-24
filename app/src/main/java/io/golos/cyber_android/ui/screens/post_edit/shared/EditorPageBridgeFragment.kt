package io.golos.cyber_android.ui.screens.post_edit.shared

interface EditorPageBridgeFragment {
    fun registerOnCloseEditorChecker(checker: (() -> Boolean)?)
}