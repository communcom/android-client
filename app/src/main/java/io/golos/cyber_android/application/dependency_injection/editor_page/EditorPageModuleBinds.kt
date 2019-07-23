package io.golos.cyber_android.application.dependency_injection.editor_page

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.common.mvvm.FragmentViewModelFactory
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class EditorPageModuleBinds {
    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory
}