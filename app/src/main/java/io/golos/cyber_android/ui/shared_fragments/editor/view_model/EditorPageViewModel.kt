package io.golos.cyber_android.ui.shared_fragments.editor.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkError
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ValidationResult
import io.golos.cyber_android.ui.shared_fragments.editor.model.EditorPageModel
import io.golos.cyber_android.ui.shared_fragments.editor.view_commands.InsertExternalLinkViewCommand
import io.golos.cyber_android.ui.shared_fragments.editor.view_commands.PostCreatedViewCommand
import io.golos.cyber_android.ui.shared_fragments.editor.view_commands.PostErrorViewCommand
import io.golos.cyber_android.ui.shared_fragments.editor.view_commands.UpdateLinkInTextViewCommand
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.utils.combinedWith
import io.golos.cyber_android.views.utils.Patterns
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Community
import io.golos.domain.entities.UploadedImageEntity
import io.golos.domain.extensions.map
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.feed.PostWithCommentUseCaseImpl
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.post.editor_output.ControlMetadata
import io.golos.domain.post.editor_output.LinkInfo
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * There can be two types of the user picked image - local and remote. Local image is the image from device camera
 * or gallery. Remote image can only appear if user choose to edit his post - this way image will be already on the remote
 * server and we don't need to upload it again.
 */
data class UserPickedImageModel(val localUri: Uri? = null, val remoteUrl: String? = null) {
    companion object {
        val EMPTY = UserPickedImageModel()
    }
}

class EditorPageViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    private val embedsUseCase: UseCase<ProccesedLinksModel>,
    private val posterUseCase: UseCase<QueryResult<DiscussionCreationResultModel>>,
    private val imageUploadUseCase: UseCase<UploadedImagesModel>,
    private val postToEdit: DiscussionIdModel?,
    private val postUseCase: PostWithCommentUseCaseImpl?,
    model: EditorPageModel
) : ViewModelBase<EditorPageModel>(
    dispatchersProvider,
    model
) {
    private var urlParserJob: Job? = null

    /**
     * Currently handled url for embed
     */
    private var currentEmbeddedLink = ""

    private var title: String = ""
    private var content: CharSequence = ""

    private var lastFile: File? = null

    private var embedCount = 0

    /**
     * State of uploading image to remote server
     */
    private val fileUploadingStateLiveData = imageUploadUseCase.getAsLiveData
        .map {
            it?.map?.get(lastFile?.absolutePath ?: "")
        }

    val getFileUploadingStateLiveData = fileUploadingStateLiveData

    val community = MutableLiveData<Community?>()
    val isPostEnabled = MutableLiveData<Boolean>(false)

    private val validationResultLiveData = MutableLiveData(false)

    /**
     * [LiveData] that indicates validness of the post content
     */
    val getValidationResultLiveData = validationResultLiveData as LiveData<Boolean>

    val isEmbedButtonsEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    /**
     * [LiveData] for image picked by user for this post. If null then there is not image.
     */
    private val attachementImageLiveData = MutableLiveData(UserPickedImageModel.EMPTY)

    val getAttachedImageLiveData = attachementImageLiveData as LiveData<UserPickedImageModel>

    /**
     * We need to "block" embed when there is image picked by user in [attachementImageLiveData]
     */
    private val embedLiveDate = MediatorLiveData<QueryResult<LinkEmbedModel>>().apply {
        addSource(embedsUseCase.getAsLiveData) {
            if (it.containsKey(currentEmbeddedLink) && getAttachedImageLiveData.value == UserPickedImageModel.EMPTY) {
                postValue(it.getValue(currentEmbeddedLink))
            }
        }

        addSource(getAttachedImageLiveData) {
            if (embedsUseCase.getAsLiveData.value?.containsKey(currentEmbeddedLink) == true
                && getAttachedImageLiveData.value == UserPickedImageModel.EMPTY
            ) {
                postValue(embedsUseCase.getAsLiveData.value?.getValue(currentEmbeddedLink))
            }
        }
    }

    /**
     * [LiveData] for result of fetching the embedded content
     */
    val getEmbedLiveDate = embedLiveDate as LiveData<QueryResult<LinkEmbedModel>>


    private val emptyEmbedLiveData = MutableLiveData(true)

    /**
     * [LiveData] that indicates if there is no embedded content on page
     */
    val getEmptyEmbedLiveData = emptyEmbedLiveData.combinedWith(getAttachedImageLiveData) { embedsEmpty, pickedImage ->
        embedsEmpty == true && pickedImage == null
    } as LiveData<Boolean>

    /**
     * [LiveData] for post creation process
     */
    val discussionCreationLiveData = posterUseCase.getAsLiveData.asEvent()


    private val nsfwLiveData = MutableLiveData(false)

    /**
     * [LiveData] for "Not Safe For Work" switch
     */
    val getNsfwLiveData = nsfwLiveData as LiveData<Boolean>

    private val postToEditLiveData = MutableLiveData<PostModel?>()

    val getPostToEditLiveData: LiveData<PostModel?> = postToEditLiveData


    private val imageUploadObserver = Observer<QueryResult<UploadedImageModel>?> { result ->
//        if (result is QueryResult.Success) {
//            if (postToEdit == null) {
//                createPost(listOf(result.originalQuery.url))
//            } else {
//                editPost(listOf(result.originalQuery.url))
//            }
//        }
    }

    private val postToEditObserver = Observer<PostModel> { postToEditLiveData.postValue(it) }

    val isInEditMode = postToEdit != null

    init {
        embedsUseCase.subscribe()
        posterUseCase.subscribe()
        postUseCase?.subscribe()
        imageUploadUseCase.subscribe()

        getFileUploadingStateLiveData.observeForever(imageUploadObserver)
        postUseCase?.getPostAsLiveData?.observeForever(postToEditObserver)

        setUp()

        //communityLiveData.postValue(CommunityModel(CommunityId("Overwatch"), "Overwatch", ""))
    }

    fun switchNSFW() {
        nsfwLiveData.value = !nsfwLiveData.value!!
    }

    fun onTitleChanged(title: String) {
        this.title = title
    }

    fun onContentChanged(content: CharSequence) {
        this.content = content
        parseUrl(content)
    }

    fun setCommunity(community: Community) {
        this.community.value = community
        isPostEnabled.value = true
    }

    /**
     * Creates new post
     */
    @Suppress("MoveVariableDeclarationIntoWhen")
    fun post(content: List<ControlMetadata>) {
        // Validate post
        val validationResult = model.validatePost(title, content)
        if(validationResult != ValidationResult.SUCCESS) {
            showValidationResult(validationResult)
            return
        }

        launch {
            try {
                command.value = SetLoadingVisibilityCommand(true)

                model.saveLastUsedCommunity(community.value!!)

                var uploadResult: UploadedImageEntity? = null
                try {
                    uploadResult = model.uploadLocalImage(content)
                } catch (ex: Exception) {
                    App.logger.log(ex)
                    command.value = ShowMessageCommand(R.string.error_upload_file)
                    return@launch
                }

                val images = uploadResult?.let { listOf(it.url) } ?: listOf()

                val adultOnly = nsfwLiveData.value == true

                try {
                    val callResult = if(postToEdit == null) {
                        model.createPost(content, adultOnly, community.value!!.id, images)
                    } else {
                        model.updatePost(content, postToEdit.permlink, adultOnly, images)
                    }
                    command.value = PostCreatedViewCommand(callResult)
                } catch (ex: Exception) {
                    App.logger.log(ex)
                    command.value = PostErrorViewCommand(ex)
                }

            } catch(ex: Exception) {
                App.logger.log(ex)
                command.value = PostErrorViewCommand(ex)
            } finally {
                command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    private fun showValidationResult(validationResult: ValidationResult) =
        when(validationResult) {
            ValidationResult.ERROR_POST_IS_TOO_LONG -> command.value = ShowMessageCommand(R.string.error_post_too_long)
            ValidationResult.ERROR_POST_IS_EMPTY -> command.value = ShowMessageCommand(R.string.error_post_empty)
            else -> throw UnsupportedOperationException("This value is not supported here: $validationResult")
        }


//    Move to model like model.createPost
//    Call result must be the same[?]
//    private fun editPost(images: List<String> = emptyList()) {
//        val tags = if (nsfwLiveData.value == true) listOf("nsfw") else emptyList()
//
//        (posterUseCase as DiscussionPosterUseCase).updatePost(
//            UpdatePostRequestModel(
//                postToEdit!!.permlink, title, content,
//                tags,
//                images
//            )
//
//        )
//    }

//    private fun createPost(images: List<String> = emptyList()) {
//        Log.d("", "")
//        val tags = if (nsfwLiveData.value == true) listOf("nsfw") else listOf()
//        val postRequest = PostCreationRequestModel(title, content, tags, images)
//        (posterUseCase as DiscussionPosterUseCase).createPostOrComment(postRequest)
//    }

    override fun onCleared() {
        super.onCleared()
        embedsUseCase.unsubscribe()
        posterUseCase.unsubscribe()
        postUseCase?.unsubscribe()
        imageUploadUseCase.unsubscribe()
        getFileUploadingStateLiveData.removeObserver(imageUploadObserver)
        postUseCase?.getPostAsLiveData?.removeObserver(postToEditObserver)
        urlParserJob?.cancel()
    }

    fun onLocalImagePicked(uri: Uri) {
        attachementImageLiveData.postValue(
            UserPickedImageModel(
                localUri = uri
            )
        )
    }

    fun clearPickedImage() {
        attachementImageLiveData.postValue(UserPickedImageModel.EMPTY)
    }

    fun onRemoteImagePicked(url: String) {
        attachementImageLiveData.postValue(
            UserPickedImageModel(
                remoteUrl = url
            )
        )
    }

    /** Stops to listen to updates of a [postToEdit]. Thus needs to be called when
     * post that is currently editing is displayed in fragment to prevent reseting screen to original state
     * on activity recreations.
     */
    fun consumePostToEdit() {
        postToEditLiveData.postValue(null)
        postUseCase?.getPostAsLiveData?.removeObserver(postToEditObserver)
    }

    fun addExternalLink(uri: String) = processUri(uri) { linkInfo ->
        InsertExternalLinkViewCommand(linkInfo)
    }

    fun checkLinkInText(isEdit: Boolean, text: String, uri: String) = processUri(uri) { linkInfo ->
        UpdateLinkInTextViewCommand(isEdit, LinkInfo(text, linkInfo.sourceUrl))
    }

    fun setEmbedCount(count: Int) {
        embedCount = count
        isEmbedButtonsEnabled.value = embedCount == 0
    }

    fun processEmbedAddedOrRemoved(isAdded: Boolean) {
        if(isAdded) {
            embedCount++
        } else {
            embedCount--
        }
        isEmbedButtonsEnabled.value = embedCount == 0
    }

    /**
     * @return null in case of error
     */
//    fun parsePostContent(content: CharSequence): PostBlock? {
//        val parsedPost = JsonToDtoMapper(App.logger).map(content.toString())
//
//        return when(parsedPost) {
//            is Either.Failure -> {
//                when(parsedPost.value) {
//                    PostParsingErrorCode.GENERAL -> command.value = ShowMessageCommand(R.string.common_general_error)
//                    PostParsingErrorCode.JSON -> command.value = ShowMessageCommand(R.string.invalid_post_format)
//                    PostParsingErrorCode.INCOMPATIBLE_VERSIONS -> command.value = ShowMessageCommand(R.string.post_processor_is_too_format)
//                }
//                null
//            }
//
//            is Either.Success -> {
//                parsedPost.value
//            }
//        }
//    }

    private fun processUri(uri: String, getSuccessViewCommand: (ExternalLinkInfo) -> ViewCommand) {
        launch {
            command.value = SetLoadingVisibilityCommand(true)

            val linkInfo = model.getExternalLinkInfo(uri)
            when(linkInfo) {
                is Either.Success -> {
                    command.value = getSuccessViewCommand(linkInfo.value)
                }

                is Either.Failure -> {
                    when(linkInfo.value) {
                        ExternalLinkError.GENERAL_ERROR -> command.value = ShowMessageCommand(R.string.common_general_error)
                        ExternalLinkError.TYPE_IS_NOT_SUPPORTED -> command.value = ShowMessageCommand(R.string.post_edit_invalid_resource_type)
                        ExternalLinkError.INVALID_URL -> command.value = ShowMessageCommand(R.string.post_edit_invalid_url)
                    }
                }
            }

            command.value = SetLoadingVisibilityCommand(false)
        }
    }

    private fun parseUrl(content: CharSequence) {
        urlParserJob?.cancel()
        urlParserJob = launch {
            delay(1_000)
            Patterns.WEB_URL.matcher(content).apply {
                if (find()) {
                    emptyEmbedLiveData.postValue(false)
                    val link = group()
                    if (currentEmbeddedLink.compareTo(link) != 0) {
                        currentEmbeddedLink = link
                        (embedsUseCase as EmbedsUseCase).requestLinkEmbedData(currentEmbeddedLink)
                    }
                } else {
                    emptyEmbedLiveData.postValue(true)
                    currentEmbeddedLink = ""
                }
            }
        }
    }

    private fun setUp() {
        launch {
            try {
                model.getLastUsedCommunity().let {
                    community.value = it
                    isPostEnabled.value = it != null
                }
            } catch (ex: Exception) {
                App.logger.log(ex)
                command.value = ShowMessageCommand(R.string.common_general_error)
                command.value = NavigateToMainScreenCommand()
            }
        }
    }
}