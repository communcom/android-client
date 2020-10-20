package io.golos.cyber_android.ui.screens.post_edit.shared

import io.golos.domain.dependency_injection.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class EditorPageBridgeImpl
@Inject
constructor(): EditorPageBridgeActivity, EditorPageBridgeFragment {
    private var closeEditorChecker: (() -> Boolean)? = null

    override fun canCloseEditor(): Boolean = closeEditorChecker?.invoke() ?: true

    override fun registerOnCloseEditorChecker(checker: (() -> Boolean)?) {
        closeEditorChecker = checker
    }
}