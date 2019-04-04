package io.golos.cyber_android.ui.screens.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.views.utils.Patterns
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.LinkEmbedModel
import io.golos.domain.interactors.model.PostCreationRequestModel
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.model.QueryResult
import kotlinx.coroutines.*


class EditorPageViewModel(
    private val embedsUseCase: EmbedsUseCase,
    private val posterUseCase: DiscussionPosterUseCase,
    dispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val urlParserJobScope = CoroutineScope(dispatchersProvider.uiDispatcher)
    private var urlParserJob: Job? = null

    /**
     * Currently handled url for embed
     */
    private var currentEmbeddedLink = ""

    private var title = ""
    private var content = ""

    private var nsfw = false
        set(value) {
            field = value
            nsfwLiveData.postValue(value)
        }

    /**
     * [LiveData] that indicates validness of the post content
     */
    val validationResultLiveData = MutableLiveData<Boolean>(false)

    /**
     * [LiveData] for result of fetching the embedded content
     */
    val embedLiveDate = MediatorLiveData<QueryResult<LinkEmbedModel>>().apply {
        addSource(embedsUseCase.getAsLiveData) {
            if (it.containsKey(currentEmbeddedLink)) {
                postValue(it.getValue(currentEmbeddedLink))
            }
        }
    }

    /**
     * [LiveData] that indicates if there is no embedded content on page
     */
    val emptyEmbedLiveData = MutableLiveData<Boolean>(true)

    /**
     * [LiveData] for post creation process
     */
    val postCreationResultLiveData = posterUseCase.getAsLiveData.asEvent()

    /**
     * [LiveData] for "Not Safe For Work" switch
     */
    val nsfwLiveData = MutableLiveData<Boolean>(nsfw)

    init {
        embedsUseCase.subscribe()
        posterUseCase.subscribe()
    }

    fun switchNSFW() {
        nsfw = !nsfw
    }

    fun onTitleChanged(title: String) {
        this.title = title
        validate(title, this.content)
    }

    fun onContentChanged(content: String) {
        this.content = content
        validate(this.title, content)
        parseUrl(content)
    }

    private fun parseUrl(content: String) {
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
     * Creates new post. Result of creation can be listened by [postCreationResultLiveData]
     */
    fun post() {
        val tags = if (nsfw) listOf("nsfw") else listOf()
        val postRequest = PostCreationRequestModel(title, content, tags)
        posterUseCase.createPostOrComment(postRequest)
    }

    private fun validate(title: String, content: String) {
        val isValid = title.trim().length > 3 && content.trim().length > 3
        validationResultLiveData.postValue(isValid)
    }

    override fun onCleared() {
        super.onCleared()
        embedsUseCase.unsubscribe()
        posterUseCase.unsubscribe()
    }
}