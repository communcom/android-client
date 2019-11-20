package io.golos.cyber_android.ui.screens.followers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.paginator.PaginalAdapter
import io.golos.utils.SPACE
import kotlinx.android.synthetic.main.item_followers.view.*

class FollowersAdapter : PaginalAdapter<Follower>() {

    override val countItemsFromEndForBeginUploadNewPage: Int = 10
    var onFollowClickedCallback: ((Follower) -> Unit)? = null

    override var items: MutableList<Follower> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROGRESS_ERROR -> getProgressErrorViewHolder(parent)
            DATA -> FollowerViewHolder(parent)
            else -> FollowerViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder.itemViewType) {
            DATA -> {
                val community = items[position]
                (holder as FollowerViewHolder).bind(community)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemViewType == DATA) {
            (holder as FollowerViewHolder).unbind()
        }
    }

    fun updateFollowers(followers: List<Follower>) {
        val calculateDiff = DiffUtil.calculateDiff(FollowersDiffUtil(this.items, followers))
        this.items.clear()
        this.items.addAll(followers)
        calculateDiff.dispatchUpdatesTo(this)
    }

    fun updateFollowingStatus(follower: Follower) {
        val position = items.indexOf(follower)
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    inner class FollowerViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_followers, parent, false)) {


        fun bind(follower: Follower) {
            setLogo(follower.avatarUrl)
            setFollowerName(follower.firstName, follower.lastName)
            setFollowingStatus(follower.isFollowing)
            setFollowingStatusClickListener()
        }

        private fun setFollowingStatusClickListener() {
            itemView.btnFollow.setOnClickListener {
                onFollowClickedCallback?.invoke(items[adapterPosition])
            }
        }

        private fun setFollowingStatus(following: Boolean) {
            val btnJoin = itemView.btnFollow
            btnJoin.isChecked = following
            val context = btnJoin.context
            if (following) {
                btnJoin.text = context.getString(R.string.following)
            } else {
                btnJoin.text = context.getString(R.string.follow)
            }
        }

        private fun setFollowerName(firstName: String, lastName: String) {
            itemView.tvName.text = firstName.plus(io.golos.utils.SPACE).plus(lastName)
        }

        private fun setLogo(logo: String?) {
            val ivLogo = itemView.ivLogo
            Glide.with(ivLogo.context)
                .load(logo)
                .apply(RequestOptions.circleCropTransform())
                .into(ivLogo)
        }

        fun unbind() {
            val ivLogo = itemView.ivLogo
            Glide.with(ivLogo.context)
                .clear(ivLogo)
        }
    }

}