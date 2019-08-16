package io.golos.cyber_android.ui.common.search

import io.golos.sharedmodel.Either

/**
 * [TR] type of an item in a search result list
 */
interface SearchEngine<TR> {
    /**
     * Starts search
     */
    fun search(searchTest: String)

    /**
     * List of result may be null in case if a search is not possible (for example, a search string is too short)
     */
    fun setOnSearchResultListener(listener: ((Either<List<TR>?, Throwable>) -> Unit)?)

    /**
     * Closes search engine
     */
    fun close()
}