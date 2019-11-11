package io.golos.cyber_android.application.dependency_injection.graph.app.ui.editor_page_fragment

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.shared_fragments.editor.model.EditorPageModel
import io.golos.cyber_android.ui.shared_fragments.editor.view_model.EditorPageViewModel
import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.Repository
import io.golos.domain.dto.*
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.feed.PostWithCommentUseCaseImpl
import io.golos.domain.use_cases.model.*
import io.golos.domain.use_cases.publish.DiscussionPosterUseCase
import io.golos.domain.use_cases.publish.EmbedsUseCase
import io.golos.domain.mappers.CommentsFeedEntityToModelMapper
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.mappers.PostEntitiesToModelMapper

@Module
class EditorPageFragmentModule(private val community: CommunityModel?, private val postToEdit: DiscussionIdModel?) {
    @Provides
    internal fun provideCommunity(): CommunityModel? = community

    @Provides
    internal fun providePostToEdit(): DiscussionIdModel? = postToEdit

    @Provides
    @IntoMap
    @ViewModelKey(EditorPageViewModel::class)
    internal fun provideEditorPageViewModel(
        embedsUseCase: EmbedsUseCase,
        posterUseCase: DiscussionPosterUseCase,
        imageUploadUseCase: UseCase<UploadedImagesModel>,
        postToEdit: DiscussionIdModel?,
        postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
        postEntityToModelMapper: PostEntitiesToModelMapper,
        commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>,
        voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
        commentFeeEntityToModelMapper: CommentsFeedEntityToModelMapper,
        dispatchersProvider: DispatchersProvider,
        model: EditorPageModel
    ): ViewModel {
        val postUseCase = if (postToEdit != null) {
            getPostWithCommentsUseCase(
                postToEdit,
                postFeedRepository,
                postEntityToModelMapper,
                commentsRepository,
                voteRepository,
                commentFeeEntityToModelMapper,
                dispatchersProvider)
        }
        else {
            null
        }

        return EditorPageViewModel(
            dispatchersProvider,
            embedsUseCase,
            posterUseCase,
            imageUploadUseCase,
            postToEdit,
            postUseCase,
            model
        )
    }

    private fun getPostWithCommentsUseCase(
        postId: DiscussionIdModel?,
        postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
        postEntityToModelMapper: PostEntitiesToModelMapper,
        commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>,
        voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
        commentFeeEntityToModelMapper: CommentsFeedEntityToModelMapper,
        dispatchersProvider: DispatchersProvider
    ) : PostWithCommentUseCaseImpl =
        PostWithCommentUseCaseImpl(
            postId!!,
            postFeedRepository,
            postEntityToModelMapper,
            commentsRepository,
            voteRepository,
            commentFeeEntityToModelMapper,
            dispatchersProvider
        )
}