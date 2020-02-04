package io.golos.cyber_android.ui.screens.profile.view.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.profile_comments.view.ProfileCommentsFragment
import io.golos.cyber_android.ui.screens.profile_posts.view.ProfilePostsExternalUserFragment
import io.golos.domain.dto.UserIdDomain

open class ProfilePagesExternalUserAdapter(
    context: Context,
    fragmentManager: FragmentManager,
    userId: UserIdDomain
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val tabTitles = context.resources.getStringArray(R.array.profile_page_tab_titles)

    private var pagesList = mutableListOf(
        ProfilePostsExternalUserFragment.newInstance(),
        ProfileCommentsFragment.newInstance(userId)
    )

    override fun getPageTitle(position: Int): CharSequence? = tabTitles.getOrNull(position)

    override fun getItem(position: Int): Fragment = pagesList[position]

    override fun getCount(): Int = pagesList.size
}