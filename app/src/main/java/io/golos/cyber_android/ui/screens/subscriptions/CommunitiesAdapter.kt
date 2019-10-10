package io.golos.cyber_android.ui.screens.subscriptions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.utils.SPACE
import kotlinx.android.synthetic.main.item_progress_error.view.*
import kotlinx.android.synthetic.main.item_subscription.view.*
import java.text.NumberFormat
import java.util.*
import kotlin.properties.Delegates

class CommunitiesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isFullData by Delegates.observable(false) { _, isFullDataOld, isFullDataNew ->
        if (isFullDataOld != isFullDataNew) {
            notifyItemChanged(communitiesList.size - 1)
        }
    }

    var nextPageCallback: (() -> Unit)? = null

    var onJoinClickedCallback: ((Community) -> Unit)? = null

    var onPageRetryLoadingCallback: (() -> Unit)? = null

    var isPageError by Delegates.observable(false) { _, isPageErrorOld, isPageErrorNew ->
        if (isPageErrorOld != isPageErrorNew) {
            val positionProgressErrorHolder = communitiesList.size - 1
            if (getItemViewType(positionProgressErrorHolder) == PROGRESS_ERROR) {
                notifyItemChanged(positionProgressErrorHolder)
            }
        }
    }

    private var communitiesList: MutableList<Community> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROGRESS_ERROR -> ProgressErrorViewHolder(parent)
            DATA -> CommunityViewHolder(parent)
            else -> CommunityViewHolder(parent)
        }
    }

    override fun getItemCount(): Int = communitiesList.size

    override fun getItemViewType(position: Int): Int {
        return if (position == communitiesList.size - 1 && !isFullData) {
            PROGRESS_ERROR
        } else {
            DATA
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            DATA -> {
                val community = communitiesList[position]
                (holder as CommunityViewHolder).bind(community)
            }
            PROGRESS_ERROR -> {
                (holder as ProgressErrorViewHolder).bind()
            }
        }
        if (!isFullData && position >= communitiesList.size - 10 && !isPageError) nextPageCallback?.invoke()
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemViewType == DATA) {
            (holder as CommunityViewHolder).unbind()
        }
    }

    fun updateCommunities(communities: MutableList<Community>) {
        val calculateDiff = DiffUtil.calculateDiff(CommunitiesDiffUtil(this.communitiesList, communities))
        this.communitiesList.clear()
        this.communitiesList.addAll(communities)
        calculateDiff.dispatchUpdatesTo(this)
    }

    fun updateSubscriptionStatus(it: Community?) {
        it?.let {
            val position = communitiesList.indexOf(it)
            if (position != -1) {
                notifyItemChanged(position)
            }
        }
    }

    inner class CommunityViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_subscription, parent, false)) {

        fun bind(community: Community) {
            setLogo(community.logo)
            setCommunityName(community.name)
            setFollowersCount(community.followersCount)
            setFollowingStatus(community.isSubscribed)
            setFollowingStatusListener()
        }

        private fun setLogo(logo: String?) {
            val ivLogo = itemView.ivLogo
            Glide.with(ivLogo.context)
                .load(logo)
                .apply(RequestOptions.circleCropTransform())
                .into(ivLogo)
        }

        private fun setCommunityName(name: String) {
            itemView.tvName.text = name
        }

        private fun setFollowersCount(followersCount: Long) {
            val pluralCount: Int = if (followersCount > 10) 10 else followersCount.toInt()
            val followersCountFormatted = NumberFormat.getNumberInstance(Locale.US).format(followersCount)
            val followersLabel = itemView.context.resources.getQuantityString(R.plurals.plural_followers, pluralCount)
            itemView.tvFollowers.text = followersCountFormatted.plus(SPACE).plus(followersLabel)
        }

        private fun setFollowingStatus(following: Boolean) {
            val btnJoin = itemView.btnJoin
            btnJoin.isChecked = following
            val context = btnJoin.context
            if (following) {
                btnJoin.text = context.getString(R.string.joined_to_community)
            } else {
                btnJoin.text = context.getString(R.string.join_to_community)
            }
        }

        private fun setFollowingStatusListener() {
            itemView.btnJoin.setOnClickListener {
                onJoinClickedCallback?.invoke(communitiesList[adapterPosition])
            }
        }

        fun unbind() {
            val ivLogo = itemView.ivLogo
            Glide.with(ivLogo.context)
                .clear(ivLogo)
        }
    }

    inner class ProgressErrorViewHolder(parent: ViewGroup) :
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

        private fun setProgressState(){
            itemView.pbPageLoading.visibility = View.VISIBLE
            itemView.btnPageLoadingRetry.visibility = View.INVISIBLE
        }

        private fun setErrorState(){
            itemView.pbPageLoading.visibility = View.INVISIBLE
            itemView.btnPageLoadingRetry.visibility = View.VISIBLE
        }
    }

    private companion object {

        @IntDef(DATA, PROGRESS_ERROR)
        @Retention(AnnotationRetention.SOURCE)
        annotation class PAGINATION_ITEM_VIEW_TYPE

        const val DATA = 0
        const val PROGRESS_ERROR = 1
    }

}