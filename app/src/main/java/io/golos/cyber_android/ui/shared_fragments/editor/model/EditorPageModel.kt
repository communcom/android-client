package io.golos.cyber_android.ui.shared_fragments.editor.model

import io.golos.cyber4j.sharedmodel.Either
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkError
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ValidationResult
import io.golos.domain.entities.UploadedImageEntity
import io.golos.domain.interactors.model.DiscussionCreationResultModel
import io.golos.domain.post.editor_output.ControlMetadata

interface EditorPageModel : ModelBase {
    suspend fun getExternalLinkInfo(uri: String): Either<ExternalLinkInfo, ExternalLinkError>

    fun validatePost(title: String, content: List<ControlMetadata>): ValidationResult

    /**
     * @return null if no image to upload otherwise - operation result
     */
    suspend fun uploadLocalImage(content: List<ControlMetadata>): Either<UploadedImageEntity, Throwable>?

    suspend fun createPost(
        content: List<ControlMetadata>,
        adultOnly: Boolean,
        localImagesUri: List<String> = emptyList()
    ): Either<DiscussionCreationResultModel, Throwable>

    suspend fun updatePost(
        content: List<ControlMetadata>,
        permlink: String,
        adultOnly: Boolean,
        localImagesUri: List<String> = emptyList()
    ): Either<DiscussionCreationResultModel, Throwable>

}