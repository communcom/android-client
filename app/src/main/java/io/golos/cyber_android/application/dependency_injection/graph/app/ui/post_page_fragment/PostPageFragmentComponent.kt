package io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.shared_fragments.post.view.PostPageFragment
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.CommentsTitleViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.SecondLevelCommentCollapsedViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments.FirstLevelCommentViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.SecondLevelCommentLoadingViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.SecondLevelCommentRetryViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments.SecondLevelCommentViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.AttachmentsWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.EmbedWebsiteWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.ParagraphWidget
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.PostPageHeaderView
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
    fun inject(header: PostPageHeaderView)

    fun inject(widget: ParagraphWidget)
    fun inject(widget: EmbedWebsiteWidget)
    fun inject(widget: AttachmentsWidget)

    fun inject(holder: CommentsTitleViewHolder)
    fun inject(holder: SecondLevelCommentLoadingViewHolder)
    fun inject(holder: SecondLevelCommentRetryViewHolder)
    fun inject(holder: FirstLevelCommentViewHolder)
    fun inject(holder: SecondLevelCommentViewHolder)
    fun inject(holder: SecondLevelCommentCollapsedViewHolder)
}