package io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.shared_fragments.post.view.PostPageFragment
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments.FirstLevelCommentViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments.SecondLevelCommentViewHolder
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    PostPageFragmentModule::class,
    PostPageFragmentModuleBinds::class
])
@FragmentScope
interface PostPageFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: PostPageFragmentModule): Builder
        fun build(): PostPageFragmentComponent
    }

    fun inject(fragment: PostPageFragment)

    fun inject(holder: FirstLevelCommentViewHolder)
    fun inject(holder: SecondLevelCommentViewHolder)
}