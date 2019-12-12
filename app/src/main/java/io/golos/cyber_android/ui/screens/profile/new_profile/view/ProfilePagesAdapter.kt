package io.golos.cyber_android.ui.screens.profile.new_profile.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.profile_comments.view.ProfileCommentsFragment
import io.golos.cyber_android.ui.screens.profile_posts.view.ProfilePostsFragment

class ProfilePagesAdapter(
    context: Context,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val tabTitles = context.resources.getStringArray(R.array.profile_page_tab_titles)

    private var pagesList = mutableListOf(
        ProfilePostsFragment.newInstance(),
        ProfileCommentsFragment.newInstance()
    )

    override fun getPageTitle(position: Int): CharSequence? = tabTitles.getOrNull(position)

    override fun getItem(position: Int): Fragment = pagesList[position]

    override fun getCount(): Int = pagesList.size
}
