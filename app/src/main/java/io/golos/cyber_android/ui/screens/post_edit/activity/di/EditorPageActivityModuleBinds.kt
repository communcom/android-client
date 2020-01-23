package io.golos.cyber_android.ui.screens.post_edit.activity.di

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.post_edit.shared.EditorPageBridgeActivity
import io.golos.cyber_android.ui.screens.post_edit.shared.EditorPageBridgeFragment
import io.golos.cyber_android.ui.screens.post_edit.shared.EditorPageBridgeImpl
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Suppress("unused")
@Module
abstract class EditorPageActivityModuleBinds {
    @Binds
    @ActivityScope
    abstract fun provideEditorPageBridgeForActivity(bridge: EditorPageBridgeImpl): EditorPageBridgeActivity

    @Binds
    @ActivityScope
    abstract fun provideEditorPageBridgeForFragment(bridge: EditorPageBridgeImpl): EditorPageBridgeFragment
}