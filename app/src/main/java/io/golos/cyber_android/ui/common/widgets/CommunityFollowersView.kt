package io.golos.cyber_android.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import kotlinx.android.synthetic.main.view_community_followers.view.*

class CommunityFollowersView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val ivFollowers by lazy { arrayOf(ivFollower0, ivFollower1, ivFollower2) }

    private val ivFollowersAwards by lazy { arrayOf(ivFollower0Award, ivFollower1Award, ivFollower2Award) }


    init {
        inflate(context, R.layout.view_community_followers, this)
    }

    fun setFollowers(followers: List<CommunityPage.CommunityFriend>) {
        val users = followers.take(MAX_USERS_COUNT)
        when(users.size){
            2 -> clFollower2.visibility = View.GONE
            1 -> {
                clFollower2.visibility = View.GONE
                clFollower1.visibility = View.GONE
            }
        }
        for ((index, follower) in followers.take(3).withIndex()) {
            Glide.with(this)
                .load(follower.avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(ivFollowers[index])
            val ivAward = ivFollowersAwards[index]
            if(follower.hasAward){
                ivAward.visibility = View.VISIBLE
            } else{
                ivAward.visibility = View.GONE
            }
        }
    }

    private companion object{

        private const val MAX_USERS_COUNT = 3
    }
}