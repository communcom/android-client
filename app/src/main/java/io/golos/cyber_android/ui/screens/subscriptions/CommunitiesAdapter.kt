package io.golos.cyber_android.ui.screens.subscriptions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.paginator.PaginalAdapter
import io.golos.cyber_android.utils.SPACE
import io.golos.cyber_android.utils.toPluralInt
import kotlinx.android.synthetic.main.item_subscription.view.*
import java.text.NumberFormat
import java.util.*

class CommunitiesAdapter : PaginalAdapter<Community>() {


    override var items: MutableList<Community> = mutableListOf()

    var onJoinClickedCallback: ((Community) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROGRESS_ERROR -> getProgressErrorViewHolder(parent)
            DATA -> CommunityViewHolder(parent)
            else -> CommunityViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder.itemViewType) {
            DATA -> {
                val community = items[position]
                (holder as CommunityViewHolder).bind(community)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemViewType == DATA) {
            (holder as CommunityViewHolder).unbind()
        }
    }

    fun updateCommunities(communities: MutableList<Community>) {
        val calculateDiff = DiffUtil.calculateDiff(CommunitiesDiffUtil(this.items, communities))
        this.items.clear()
        this.items.addAll(communities)
        calculateDiff.dispatchUpdatesTo(this)
    }

    fun updateSubscriptionStatus(it: Community?) {
        it?.let {
            val position = items.indexOf(it)
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
            val followersCountFormatted = NumberFormat.getNumberInstance(Locale.US).format(followersCount)
            val followersLabel = itemView.context.resources.getQuantityString(R.plurals.plural_followers, followersCount.toPluralInt())
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
                onJoinClickedCallback?.invoke(items[adapterPosition])
            }
        }

        fun unbind() {
            val ivLogo = itemView.ivLogo
            Glide.with(ivLogo.context)
                .clear(ivLogo)
        }
    }
}