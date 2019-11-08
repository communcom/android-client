package io.golos.cyber_android.ui.screens.postslist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class GetPostsConfiguration

@Parcelize
data class GetPostsByUserIdConfiguration(val userId: String) : GetPostsConfiguration(), Parcelable

@Parcelize
data class GetPostsByCommunityConfiguration(val community: String) : GetPostsConfiguration(), Parcelable

@Parcelize
data class GetPostsByCommunityAliasConfiguration(val communityAlias: String) : GetPostsConfiguration(), Parcelable