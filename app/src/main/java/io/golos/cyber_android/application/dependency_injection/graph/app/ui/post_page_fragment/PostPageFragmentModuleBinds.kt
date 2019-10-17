package io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.shared_fragments.editor.model.EditorPageModel
import io.golos.cyber_android.ui.shared_fragments.editor.model.EditorPageModelImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.PostPageModel
import io.golos.cyber_android.ui.shared_fragments.post.model.PostPageModelImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSource
import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceImpl
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModel
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.feed.PostWithCommentUseCaseImpl

@Module
abstract class PostPageFragmentModuleBinds {
    @Binds
    abstract fun providePostWithCommentsUseCase(useCase: PostWithCommentUseCaseImpl): PostWithCommentUseCase

    @Binds
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(PostPageViewModel::class)
    internal abstract fun providePostPageViewModel(viewModel: PostPageViewModel): ViewModel

    @Binds
    abstract fun bindModel(model: PostPageModelImpl): PostPageModel

    @Binds
    abstract fun bindPostListDataSource(dataSource: PostListDataSourceImpl): PostListDataSource
}