package io.golos.cyber_android.ui.common.paginator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.subscriptions.Community
import kotlinx.android.synthetic.main.item_progress_error.view.*
import kotlin.properties.Delegates

/**
 * Adapter for realization base pagination functional
 */
abstract class PaginalAdapter<ITEMS> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract var items: MutableList<ITEMS>

    var isFullData by Delegates.observable(false) { _, isFullDataOld, isFullDataNew ->
        if (isFullDataOld != isFullDataNew) {
            notifyItemChanged(items.size - 1)
        }
    }

    var nextPageCallback: (() -> Unit)? = null

    var onJoinClickedCallback: ((Community) -> Unit)? = null

    var onPageRetryLoadingCallback: (() -> Unit)? = null

    var isPageError by Delegates.observable(false) { _, isPageErrorOld, isPageErrorNew ->
        if (isPageErrorOld != isPageErrorNew) {
            val positionProgressErrorHolder = items.size - 1
            if (getItemViewType(positionProgressErrorHolder) == PROGRESS_ERROR) {
                notifyItemChanged(positionProgressErrorHolder)
            }
        }
    }

    var isSearchProgress by Delegates.observable(false) { _, isSearchProgressOld, isSearchProgressNew ->
        if (isSearchProgressOld != isSearchProgressNew) {
            val lastPositionItem = items.size - 1
            notifyItemChanged(lastPositionItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size - 1 && !isFullData) {
            PROGRESS_ERROR
        } else {
            DATA
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            PROGRESS_ERROR -> {
                (holder as PaginalAdapter<*>.ProgressErrorViewHolder).bind()
            }
        }
        if (!isFullData && position >= items.size - 10 && !isPageError && !isSearchProgress) nextPageCallback?.invoke()
    }

    protected fun getProgressErrorViewHolder(parent: ViewGroup): ProgressErrorViewHolder {
        return ProgressErrorViewHolder(parent)
    }

    protected inner class ProgressErrorViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_progress_error, parent, false)) {

        fun bind() {
            if (isPageError) {
                setErrorState()
            } else {
                setProgressState()
            }
            itemView.btnPageLoadingRetry.setOnClickListener {
                onPageRetryLoadingCallback?.let {
                    setProgressState()
                    it.invoke()
                }
            }
        }

        private fun setProgressState() {
            itemView.pbPageLoading.visibility = View.VISIBLE
            itemView.btnPageLoadingRetry.visibility = View.INVISIBLE
        }

        private fun setErrorState() {
            itemView.pbPageLoading.visibility = View.INVISIBLE
            itemView.btnPageLoadingRetry.visibility = View.VISIBLE
        }
    }

    protected companion object {

        @IntDef(DATA, PROGRESS_ERROR)
        @Retention(AnnotationRetention.SOURCE)
        protected annotation class PAGINATION_ITEM_VIEW_TYPE

        const val DATA = 0
        const val PROGRESS_ERROR = 1
    }
}