package io.golos.cyber_android.ui.screens.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.ValidationConstants
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.views.utils.Patterns
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class EditorPageViewModel(
    private val embedsUseCase: EmbedsUseCase,
    private val posterUseCase: DiscussionPosterUseCase,
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


    private val embedLiveDate = MediatorLiveData<QueryResult<LinkEmbedModel>>().apply {
        addSource(embedsUseCase.getAsLiveData) {
            if (it.containsKey(currentEmbeddedLink)) {
                postValue(it.getValue(currentEmbeddedLink))
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
    val getEmptyEmbedLiveData = emptyEmbedLiveData as LiveData<Boolean>

    /**
     * [LiveData] for post creation process
     */
    val discussionCreationLiveData = posterUseCase.getAsLiveData.asEvent()


    private val nsfwLiveData = MutableLiveData<Boolean>(false)

    /**
     * [LiveData] for "Not Safe For Work" switch
     */
    val getNsfwLiveData = nsfwLiveData as LiveData<Boolean>


    val getPostToEditLiveData: LiveData<PostModel> = postUseCase?.getPostAsLiveData ?: MutableLiveData()

    init {
        embedsUseCase.subscribe()
        posterUseCase.subscribe()
        postUseCase?.subscribe()

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
            if (postToEdit == null) createPost() else editPost()
        }
    }

    private fun editPost() {
        TODO()
    }

    private fun createPost() {
        val tags = if (nsfwLiveData.value == true) listOf("nsfw") else listOf()
        val postRequest = PostCreationRequestModel(title, content, tags)
        posterUseCase.createPostOrComment(postRequest)
    }

    private fun validate(title: CharSequence, content: CharSequence): Boolean {
        val isValid = content.isNotBlank() && content.length <= ValidationConstants.MAX_POST_CONTENT_LENGTH
                && title.trim().isNotEmpty() && title.length <= ValidationConstants.MAX_POST_TITLE_LENGTH
                && (postToEdit == null
                || (title != getPostToEditLiveData.value?.content?.title
                || content != getPostToEditLiveData.value?.content?.body?.toContent()))
        validationResultLiveData.postValue(isValid)
        return isValid
    }

    override fun onCleared() {
        super.onCleared()
        embedsUseCase.unsubscribe()
        posterUseCase.unsubscribe()
        postUseCase?.unsubscribe()
    }
}

internal fun ContentBodyModel.toContent(): CharSequence = this.full.fold(StringBuilder()) { acc, row ->
    if (acc.isNotEmpty()) acc.append("\n")
    when (row) {
        is TextRowModel -> acc.append(row.text)
    }
    acc
}