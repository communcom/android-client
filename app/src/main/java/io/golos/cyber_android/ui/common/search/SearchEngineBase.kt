package io.golos.cyber_android.ui.common.search

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.application.App
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

/**
 * Skeleton for a search by text string
 * [TR] type of an item in a search result list
 */
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
abstract class SearchEngineBase<TR>(
    private val dispatchersProvider: DispatchersProvider
): CoroutineScope, SearchEngine<TR> {

    private var searchChannel: ConflatedBroadcastChannel<String>? = null

    private var searchResultListener: ((Either<List<TR>?, Throwable>) -> Unit)? = null

    private var isSearchSetUp = false

    private var searchJob: Job? = null

    override val coroutineContext: CoroutineContext
    get() = dispatchersProvider.uiDispatcher

    /**
     * Starts search
     */
    override fun search(searchTest: String) {
        if(!isSearchSetUp) {
            initTypingSearch()
        }

        searchChannel?.offer(searchTest)
    }

    /**
     * List of result may be null in case if a search is not possible (for example, a search string is too short)
     */
    override fun setOnSearchResultListener(listener: ((Either<List<TR>?, Throwable>) -> Unit)?) {
        searchResultListener = listener
    }

    /**
     * Closes search engine
     */
    override fun close() {
        searchChannel?.close()
        searchJob?.cancel()

        searchChannel = null
        searchJob = null
    }

    protected abstract suspend fun doSearch(searchString: String): List<TR>?

    private fun initTypingSearch() {
        searchJob = launch {
            searchChannel = ConflatedBroadcastChannel()

            searchChannel?.consumeEach { searchString ->
                delay(500)

                try {
                    val searchResult = withContext(Dispatchers.IO) {
                        doSearch(searchString)
                    }

                    searchResultListener?.invoke(Either.Success<List<TR>?, Throwable>(searchResult))
                }
                catch (ex: Exception) {
                    App.logger.log(ex)
                    searchResultListener?.invoke(Either.Failure<List<TR>?, Throwable>(ex))
                }
            }
        }

        isSearchSetUp = true
    }
}