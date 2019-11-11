package io.golos.cyber_android.ui.screens.postslist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class GetPostsConfiguration(open var userId: String)

@Parcelize
data class GetPostsByUserIdConfiguration(override var userId: String) : GetPostsConfiguration(userId), Parcelable

@Parcelize
data class GetPostsByCommunityConfiguration(override var userId: String, val community: String) : GetPostsConfiguration(userId), Parcelable

@Parcelize
data class GetPostsByCommunityAliasConfiguration(override var  userId: String, val communityAlias: String) : GetPostsConfiguration(userId), Parcelable