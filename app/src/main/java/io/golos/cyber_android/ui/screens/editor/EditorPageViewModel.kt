package io.golos.cyber_android.ui.screens.editor

import android.net.Uri
import androidx.lifecycle.*
import io.golos.cyber_android.utils.Event
import io.golos.cyber_android.utils.ValidationConstants
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.utils.combinedWith
import io.golos.cyber_android.views.utils.Patterns
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.images.ImageUploadUseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.map
import io.golos.domain.requestmodel.CompressionParams
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

data class UserPickedImageModel(val localUri: Uri? = null, val remoteUrl: String? = null) {
    companion object {
        val EMPTY = UserPickedImageModel()
    }
}

class EditorPageViewModel(
    private val embedsUseCase: EmbedsUseCase,
    private val posterUseCase: DiscussionPosterUseCase,
    private val imageUploadUseCase: ImageUploadUseCase,
    dispatchersProvider: DispatchersProvider,
    community: CommunityModel?,
    private val postToEdit: DiscussionIdModel?,
    private val postUseCase: PostWithCommentUseCase?
) : ViewModel() {

    private val urlParserJobScope = CoroutineScope(dispatchersProvider.uiDispatcher)
    private var urlParserJob: Job? = null

    /**
     * Currently handled url for embed
     */
    private var currentEmbeddedLink = ""

    private var title: String = ""
    private var content: CharSequence = ""

    private var lastFile: File? = null
    /**
     * State of uploading image to remote server
     */


    private val fileUploadingStateLiveData = imageUploadUseCase.getAsLiveData
        .map {
            it?.map?.get(lastFile?.absolutePath ?: "")
        }.asEvent()

    val getFileUploadingStateLiveData = fileUploadingStateLiveData


    private val communityLiveData = MutableLiveData<CommunityModel?>().apply {
        postValue(community)
    }

    /**
     * [LiveData] for community that post will be created in
     */
    val getCommunityLiveData = communityLiveData as LiveData<CommunityModel?>


    private val validationResultLiveData = MutableLiveData<Boolean>(false)

    /**
     * [LiveData] that indicates validness of the post content
     */
    val getValidationResultLiveData = validationResultLiveData as LiveData<Boolean>


    /**
     * [LiveData] for image picked by user for this post. If null then there is not image.
     */
    private val attachementImageLiveData = MutableLiveData<UserPickedImageModel>(UserPickedImageModel.EMPTY)

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


    private val emptyEmbedLiveData = MutableLiveData<Boolean>(true)

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


    private val nsfwLiveData = MutableLiveData<Boolean>(false)

    /**
     * [LiveData] for "Not Safe For Work" switch
     */
    val getNsfwLiveData = nsfwLiveData as LiveData<Boolean>

    private val postToEditLiveData = MutableLiveData<PostModel?>()

    val getPostToEditLiveData: LiveData<PostModel?> = postToEditLiveData


    private val imageUploadObserver = Observer<Event<QueryResult<UploadedImageModel>?>> {
        it.getIfNotHandled()?.let { result ->
            if (result is QueryResult.Success) {
                if (validate(title, content)) {
                    if (postToEdit == null) createPost(listOf(result.originalQuery.url)) else editPost(listOf(result.originalQuery.url))
                }
            }
        }
    }

    private val postToEditObserver = Observer<PostModel> {
        if (it.content.body.full.isNotEmpty())
            postToEditLiveData.postValue(it)
    }

    init {
        embedsUseCase.subscribe()
        posterUseCase.subscribe()
        postUseCase?.subscribe()
        imageUploadUseCase.subscribe()

        getFileUploadingStateLiveData.observeForever(imageUploadObserver)
        postUseCase?.getPostAsLiveData?.observeForever(postToEditObserver)

        communityLiveData.postValue(CommunityModel(CommunityId("Overwatch"), "Overwatch", ""))
    }

    fun switchNSFW() {
        nsfwLiveData.value = !nsfwLiveData.value!!
    }

    fun onTitleChanged(title: String) {
        this.title = title
        validate(title, this.content)
    }

    fun onContentChanged(content: CharSequence) {
        this.content = content
        validate(this.title, content)
        parseUrl(content)
    }

    private fun parseUrl(content: CharSequence) {
        urlParserJob?.cancel()
        urlParserJob = urlParserJobScope.launch {
            delay(1_000)
            Patterns.WEB_URL.matcher(content).apply {
                if (find()) {
                    emptyEmbedLiveData.postValue(false)
                    val link = group()
                    if (currentEmbeddedLink.compareTo(link) != 0) {
                        currentEmbeddedLink = link
                        embedsUseCase.requestLinkEmbedData(currentEmbeddedLink)
                    }
                } else {
                    emptyEmbedLiveData.postValue(true)
                    currentEmbeddedLink = ""
                }
            }
        }
    }

    /**
     * Creates new post. Result of creation can be listened by [discussionCreationLiveData]
     */
    fun post() {
        if (validate(title, content)) {

            viewModelScope.launch {
                getAttachedImageLiveData.value?.localUri?.let { uri ->
                    val imageFile = File(uri.path)
                    imageUploadUseCase.submitImageForUpload(
                        imageFile.absolutePath,
                        CompressionParams.DirectCompressionParams
                    )
                    lastFile = imageFile
                }

                //if there is no image to upload we create post immediately
                if (getAttachedImageLiveData.value == UserPickedImageModel.EMPTY) {
                    if (postToEdit == null) createPost() else editPost()
                }

                //if there is image to upload, but it was already attached to a post (this can only
                //happens when postToEdit != null and thus user actually editing post, not creating), then
                //just attach this photo again
                if (getAttachedImageLiveData.value != UserPickedImageModel.EMPTY &&
                    getAttachedImageLiveData.value?.remoteUrl != null
                ) {
                    val remoteImg = getAttachedImageLiveData.value?.remoteUrl ?: ""
                    if (postToEdit == null) createPost(listOf(remoteImg)) else editPost(listOf(remoteImg))
                }

            }
        }
    }


    private fun editPost(images: List<String> = emptyList()) {
        val tags = if (nsfwLiveData.value == true) listOf("nsfw") else emptyList()

        posterUseCase.updatePost(
            UpdatePostRequestModel(
                postToEdit!!.permlink, title, content,
                tags,
                images
            )

        )
    }

    private fun createPost(images: List<String> = emptyList()) {
        val tags = if (nsfwLiveData.value == true) listOf("nsfw") else listOf()
        val postRequest = PostCreationRequestModel(title, content, tags, images)
        posterUseCase.createPostOrComment(postRequest)
    }

    private fun validate(title: CharSequence, content: CharSequence): Boolean {
        val isValid = content.isNotBlank() && content.length <= ValidationConstants.MAX_POST_CONTENT_LENGTH
                && title.isNotBlank() && title.length <= ValidationConstants.MAX_POST_TITLE_LENGTH
        validationResultLiveData.postValue(isValid)
        return isValid
    }

    override fun onCleared() {
        super.onCleared()
        embedsUseCase.unsubscribe()
        posterUseCase.unsubscribe()
        postUseCase?.unsubscribe()
        imageUploadUseCase.unsubscribe()
        getFileUploadingStateLiveData.removeObserver(imageUploadObserver)
        postUseCase?.getPostAsLiveData?.removeObserver(postToEditObserver)
    }

    fun onLocalImagePicked(uri: Uri) {
        attachementImageLiveData.postValue(UserPickedImageModel(localUri = uri))
    }

    fun clearPickedImage() {
        attachementImageLiveData.postValue(UserPickedImageModel.EMPTY)
    }

    fun onRemoteImagePicked(url: String) {
        attachementImageLiveData.postValue(UserPickedImageModel(remoteUrl = url))
    }

    /** Stops to listen to updates of a [postToEdit]. Thus needs to be called when
     * post that is currently editing is displayed in fragment to prevent reseting screen to original state
     * on activity recreations.
     */
    fun consumePostToEdit() {
        postToEditLiveData.postValue(null)
        postUseCase?.getPostAsLiveData?.removeObserver(postToEditObserver)
    }
}

internal fun ContentBodyModel.toContent(): CharSequence = this.full
    .filter { it is TextRowModel }.joinToString("\n") { (it as TextRowModel).text }