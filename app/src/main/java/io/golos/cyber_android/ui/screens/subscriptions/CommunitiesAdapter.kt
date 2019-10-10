package io.golos.cyber_android.ui.screens.subscriptions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_subscription.view.*

class CommunitiesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isFullData: Boolean = false

    var nextPageCallback: (() -> Unit)? = null

    var onJoinClickedCallback: ((Community) -> Unit)? = null

    private var communitiesList: MutableList<Community> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            PROGRESS -> ProgressViewHolder(parent)
            ITEM -> CommunityViewHolder(parent)
            else -> CommunityViewHolder(parent)
        }
    }

    override fun getItemCount(): Int = communitiesList.size

    override fun getItemViewType(position: Int): Int {
        return if(position == communitiesList.size - 1){
            PROGRESS
        } else {
            ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == ITEM){
            val community = communitiesList[position]
            (holder as CommunityViewHolder).bind(community)
        }
        if (!isFullData && position >= communitiesList.size - 10) nextPageCallback?.invoke()
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if(holder.itemViewType == ITEM){
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

        fun bind(community: Community){
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
            itemView.tvFollowers.text = followersCount.toString()
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

    inner class ProgressViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))

    inner class ErrorViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))

    private companion object {

        @IntDef(ITEM, PROGRESS, ERROR)
        @Retention(AnnotationRetention.SOURCE)
        annotation class PAGINATION_ITEM_VIEW_TYPE

        const val ITEM = 0
        const val PROGRESS = 1
        const val ERROR = 2
    }

}