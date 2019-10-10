package io.golos.cyber_android.ui.screens.subscriptions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_subscription.view.*

class CommunitiesAdapter : RecyclerView.Adapter<CommunitiesAdapter.RecommendedCommunitiesViewHolder>() {

    var isFullData: Boolean = false

    var nextPageCallback: (() -> Unit)? = null

    var onJoinClickedCallback: ((Community) -> Unit)? = null

    private var communitiesList: MutableList<Community> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedCommunitiesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subscription, parent, false)
        return RecommendedCommunitiesViewHolder(view)
    }

    override fun getItemCount(): Int = communitiesList.size

    override fun onBindViewHolder(holder: RecommendedCommunitiesViewHolder, position: Int) {
        val community = communitiesList[position]
        holder.setLogo(community.logo)
        holder.setCommunityName(community.name)
        holder.setFollowersCount(community.followersCount)
        holder.setFollowingStatus(community.isSubscribed)
        holder.setFollowingStatusListener()
        if (!isFullData && position >= communitiesList.size - 10) nextPageCallback?.invoke()
    }

    override fun onViewRecycled(holder: RecommendedCommunitiesViewHolder) {
        super.onViewRecycled(holder)
        val ivLogo = holder.itemView.ivLogo
        Glide.with(ivLogo.context)
            .clear(ivLogo)
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
            if(position != -1){
                notifyItemChanged(position)
            }
        }
    }

    inner class RecommendedCommunitiesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setLogo(logo: String?) {
            val ivLogo = itemView.ivLogo
            Glide.with(ivLogo.context)
                .load(logo)
                .apply(RequestOptions.circleCropTransform())
                .into(ivLogo)
        }

        fun setCommunityName(name: String) {
            itemView.tvName.text = name
        }

        fun setFollowersCount(followersCount: Long) {
            itemView.tvFollowers.text = followersCount.toString()
        }

        fun setFollowingStatus(following: Boolean) {
            val btnJoin = itemView.btnJoin
            btnJoin.isChecked = following
            val context = btnJoin.context
            if (following) {
                btnJoin.text = context.getString(R.string.joined_to_community)
            } else {
                btnJoin.text = context.getString(R.string.join_to_community)
            }
        }

        fun setFollowingStatusListener() {
            itemView.btnJoin.setOnClickListener {
                onJoinClickedCallback?.invoke(communitiesList[adapterPosition])
            }
        }

    }
}