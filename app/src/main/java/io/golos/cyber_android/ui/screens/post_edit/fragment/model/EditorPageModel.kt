package io.golos.cyber_android.ui.screens.post_edit.fragment.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ExternalLinkError
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ValidationResult
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.UploadedImageEntity
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.ControlMetadata

interface EditorPageModel : ModelBase {
    suspend fun getExternalLinkInfo(uri: String): Either<ExternalLinkInfo, ExternalLinkError>

    fun validatePost(title: String, content: List<ControlMetadata>): ValidationResult

    /**
     * @return null if no image to upload otherwise - operation contentId
     */
    suspend fun uploadLocalImage(content: List<ControlMetadata>): UploadedImageEntity?

    suspend fun createPost(
        title:String,
        content: List<ControlMetadata>,
        adultOnly: Boolean,
        communityId: CommunityIdDomain,
        localImagesUri: List<String> = emptyList()
    ): ContentIdDomain

    suspend fun updatePost(
        title:String,
        contentIdDomain: ContentIdDomain,
        content: List<ControlMetadata>,
        permlink: Permlink,
        adultOnly: Boolean,
        localImagesUri: List<String> = emptyList()
    ): ContentIdDomain

    suspend fun getPostToEdit(contentId: ContentIdDomain): PostDomain
}