package io.golos.cyber_android.ui.screens.post_edit.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.mappers.mapToContentId
import io.golos.cyber_android.ui.mappers.mapToContentIdDomain
import io.golos.cyber_android.ui.screens.post_edit.dto.ExternalLinkError
import io.golos.cyber_android.ui.screens.post_edit.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.screens.post_edit.dto.ValidationResult
import io.golos.cyber_android.ui.screens.post_edit.model.EditorPageModel
import io.golos.cyber_android.ui.screens.post_edit.view_commands.*
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.UploadedImageEntity
import io.golos.domain.extensions.map
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.feed.PostWithCommentUseCaseImpl
import io.golos.domain.use_cases.model.*
import io.golos.domain.use_cases.post.editor_output.ControlMetadata
import io.golos.domain.use_cases.post.editor_output.LinkInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val postUseCase: PostWithCommentUseCaseImpl?,
    private val contentId: ContentId?,
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

    val community = MutableLiveData<CommunityDomain?>()
    val isPostEnabled = MutableLiveData<Boolean>(false)
    val isSelectCommunityEnabled = MutableLiveData<Boolean>(false)

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


    private val emptyEmbedLiveData = MutableLiveData(true)

    private val nsfwLiveData = MutableLiveData(false)

    /**
     * [LiveData] for "Not Safe For Work" switch
     */
    val getNsfwLiveData = nsfwLiveData as LiveData<Boolean>

    val editingPost = MutableLiveData<PostDomain?>()

    private val imageUploadObserver = Observer<QueryResult<UploadedImageModel>?> { }

    val isInEditMode = contentId != null

    init {
        embedsUseCase.subscribe()
        posterUseCase.subscribe()

        imageUploadUseCase.subscribe()

        getFileUploadingStateLiveData.observeForever(imageUploadObserver)

        setUp()
    }

    fun switchNSFW() {
        nsfwLiveData.value = !nsfwLiveData.value!!
    }

    fun onTitleChanged(title: String) {
        this.title = title
    }

    fun setCommunity(community: CommunityDomain) {
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
        if (validationResult != ValidationResult.SUCCESS) {
            showValidationResult(validationResult)
            return
        }

        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)

                model.saveLastUsedCommunity(community.value!!)

                var uploadResult: UploadedImageEntity? = null
                try {
                    uploadResult = model.uploadLocalImage(content)
                } catch (ex: Exception) {
                    Timber.e(ex)
                    _command.value = ShowMessageResCommand(R.string.error_upload_file)
                    return@launch
                }

                val images = uploadResult?.let { listOf(it.url) } ?: listOf()

                val adultOnly = nsfwLiveData.value == true

                try {
                    val callResult = if (!isInEditMode) {
                        model.createPost(
                            content,
                            adultOnly,
                            CommunityId(community.value!!.communityId),
                            images
                        )
                    } else {
                        model.updatePost(
                            contentId!!.mapToContentIdDomain(),
                            content,
                            Permlink(contentId.permlink),
                            adultOnly,
                            images
                        )
                    }
                    _command.value = PostCreatedViewCommand(callResult.mapToContentId())
                } catch (ex: Exception) {
                    Timber.e(ex)
                    _command.value = PostErrorViewCommand(ex)
                }

            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = PostErrorViewCommand(ex)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    private fun showValidationResult(validationResult: ValidationResult) =
        when (validationResult) {
            ValidationResult.ERROR_POST_IS_TOO_LONG -> _command.value = ShowMessageResCommand(R.string.error_post_too_long)
            ValidationResult.ERROR_POST_IS_EMPTY -> _command.value = ShowMessageResCommand(R.string.error_post_empty)
            else -> throw UnsupportedOperationException("This value is not supported here: $validationResult")
        }

    override fun onCleared() {
        super.onCleared()
        embedsUseCase.unsubscribe()
        posterUseCase.unsubscribe()
        postUseCase?.unsubscribe()
        imageUploadUseCase.unsubscribe()
        getFileUploadingStateLiveData.removeObserver(imageUploadObserver)
        //postUseCase?.getPostAsLiveData?.removeObserver(postToEditObserver)
        urlParserJob?.cancel()
    }

    fun checkLinkInText(isEdit: Boolean, text: String, uri: String) = processUri(uri) { linkInfo ->
        _command.value = UpdateLinkInTextViewCommand(isEdit, LinkInfo(text, linkInfo.sourceUrl))

        if (!isEdit && embedCount == 0) {
            _command.value = InsertExternalLinkViewCommand(linkInfo)
        }
    }

    fun validatePastedLink(uri: Uri) = processUri(uri.toString()) {
        _command.value = PastedLinkIsValidViewCommand(uri)

        if (embedCount == 0) {
            _command.value = InsertExternalLinkViewCommand(it)
        }
    }

    fun setEmbedCount(count: Int) {
        embedCount = count
        isEmbedButtonsEnabled.value = embedCount == 0
    }

    fun processEmbedAddedOrRemoved(isAdded: Boolean) {
        if (isAdded) {
            embedCount++
        } else {
            embedCount--
        }
        isEmbedButtonsEnabled.value = embedCount == 0
    }

    private fun processUri(uri: String, processSuccessViewCommand: (ExternalLinkInfo) -> Unit) {
        launch {
            _command.value = SetLoadingVisibilityCommand(true)

            when (val linkInfo = model.getExternalLinkInfo(uri)) {
                is Either.Success -> {
                    processSuccessViewCommand(linkInfo.value)
                }

                is Either.Failure -> {
                    when (linkInfo.value) {
                        ExternalLinkError.GENERAL_ERROR -> _command.value = ShowMessageResCommand(R.string.common_general_error)
                        ExternalLinkError.TYPE_IS_NOT_SUPPORTED -> _command.value =
                            ShowMessageResCommand(R.string.post_edit_invalid_resource_type)
                        ExternalLinkError.INVALID_URL -> _command.value = ShowMessageResCommand(R.string.post_edit_invalid_url)
                    }
                }
            }

            _command.value = SetLoadingVisibilityCommand(false)
        }
    }

    private fun setUp() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                isSelectCommunityEnabled.value = !isInEditMode

                if (contentId == null) {
                    // New post
                    val lastUsedCommunity = model.getLastUsedCommunity()
                    community.value = lastUsedCommunity
                    isPostEnabled.value = lastUsedCommunity != null
                } else {
                    // Updated post
                    val postToEditCall = async { model.getPostToEdit(contentId) }

                    val postToEdit = postToEditCall.await()
                    val communityDomain = postToEdit.community
                    community.value = CommunityDomain(
                        communityDomain.communityId,
                        communityDomain.alias,
                        communityDomain.name ?: "",
                        communityDomain.avatarUrl,
                        null,
                        0,
                        0,
                        communityDomain.isSubscribed
                    )
                    isPostEnabled.value = true
                    editingPost.value = postToEdit
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
                _command.value = NavigateToMainScreenCommand()
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }
}