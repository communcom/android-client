package io.golos.cyber_android.ui.shared.paginator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_progress_error.view.*
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * Adapter for realization base pagination functional
 */
abstract class PaginalAdapter<ITEM> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract var items: MutableList<ITEM>

    var isFullData by Delegates.observable(false) { _, isFullDataOld, isFullDataNew ->
        if (isFullDataOld != isFullDataNew) {
            notifyItemChanged(items.size - 1)
        }
    }

    var nextPageCallback: (() -> Unit)? = null

    var onPageRetryLoadingCallback: (() -> Unit)? = null

    var isPageError by Delegates.observable(false) { _, isPageErrorOld, isPageErrorNew ->
        if (isPageErrorOld != isPageErrorNew) {
            val positionProgressErrorHolder = items.size - 1
            if (getItemViewType(positionProgressErrorHolder) == PROGRESS_ERROR) {
                notifyItemChanged(positionProgressErrorHolder)
            }
        }
    }

    var isNewPageProgress by Delegates.observable(false) { _, isSearchProgressOld, isSearchProgressNew ->
        if (isSearchProgressOld != isSearchProgressNew) {
            val lastPositionItem = items.size - 1
            try {
                notifyItemChanged(lastPositionItem)
            }catch (e: Exception){
                Timber.e(e)
            }
        }
    }

    override fun getItemCount(): Int = items.size

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
        if (!isFullData && position >= items.size - countItemsFromEndForBeginUploadNewPage && !isPageError && !isNewPageProgress) {
            nextPageCallback?.invoke()
        }
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

    /**
     * Count items in list for begin upload new page in list
     */
    protected abstract val countItemsFromEndForBeginUploadNewPage: Int

    protected companion object {

        const val DATA = 0
        const val PROGRESS_ERROR = 1
    }

}