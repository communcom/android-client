package io.golos.cyber_android.ui.screens.discovery.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.golos.cyber_android.R
import io.golos.cyber_android.databinding.AdapterSearchResultPreviewItemBinding
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.CommunityListItem
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain

class DiscoveryRecyclerAdapter(
    private val onUserClick:(UserIdDomain)->Unit,
    private val onCommunityClick:(CommunityIdDomain)->Unit
) : ListAdapter<Any, DiscoveryRecyclerAdapter.ViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterSearchResultPreviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(currentList[position] is CommunityListItem){
            Glide.with(holder.mBinding.root.context)
                .load((currentList[position] as CommunityListItem).community.avatarUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_commun)
                .circleCrop()
                .into(holder.mBinding.image)
            holder.mBinding.name.text = (currentList[position] as CommunityListItem).community.name
            holder.mBinding.root.setOnClickListener { onCommunityClick((currentList[position] as CommunityListItem).community.communityId) }
        }else{
            Glide.with(holder.mBinding.root.context)
                .load((currentList[position] as FollowersListItem).follower.userAvatar)
                .centerCrop()
                .placeholder(R.drawable.ic_avatar)
                .circleCrop()
                .into(holder.mBinding.image)
            holder.mBinding.name.text = (currentList[position] as FollowersListItem).follower.userName
            holder.mBinding.root.setOnClickListener { onUserClick((currentList[position] as FollowersListItem).follower.userId) }
        }
    }

    private class ItemCallback : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is CommunityListItem && newItem is CommunityListItem) return oldItem.id == newItem.id
            if (oldItem is FollowersListItem && newItem is FollowersListItem) return oldItem.id == newItem.id
            return false
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return false
        }
    }

    class ViewHolder(val mBinding: AdapterSearchResultPreviewItemBinding) : RecyclerView.ViewHolder(mBinding.root)

}