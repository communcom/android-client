package io.golos.cyber_android.ui.shared_fragments.editor.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkError
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ValidationResult
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.UploadedImageEntity
import io.golos.domain.use_cases.model.DiscussionCreationResultModel
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.post.editor_output.ControlMetadata

interface EditorPageModel : ModelBase {
    suspend fun getExternalLinkInfo(uri: String): Either<ExternalLinkInfo, ExternalLinkError>

    fun validatePost(title: String, content: List<ControlMetadata>): ValidationResult

    /**
     * @return null if no image to upload otherwise - operation result
     */
    suspend fun uploadLocalImage(content: List<ControlMetadata>): UploadedImageEntity?

    suspend fun createPost(
        content: List<ControlMetadata>,
        adultOnly: Boolean,
        communityId: CommunityId,
        localImagesUri: List<String> = emptyList()
    ): DiscussionCreationResultModel

    suspend fun updatePost(
        content: List<ControlMetadata>,
        permlink: Permlink,
        adultOnly: Boolean,
        localImagesUri: List<String> = emptyList()
    ): DiscussionCreationResultModel

    suspend fun getLastUsedCommunity(): CommunityDomain?

    suspend fun saveLastUsedCommunity(community: CommunityDomain)

    suspend fun getPostToEdit(contentId: ContentId): PostDomain

    @Deprecated("Use getPostToEdit with contentId param")
    suspend fun getPostToEdit(permlink: Permlink): PostModel
}