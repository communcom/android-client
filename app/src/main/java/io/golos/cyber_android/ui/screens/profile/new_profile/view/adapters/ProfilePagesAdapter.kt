package io.golos.cyber_android.ui.screens.profile.new_profile.view.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.profile_comments.view.ProfileCommentsFragment
import io.golos.cyber_android.ui.screens.profile_posts.view.ProfilePostsFragment
import io.golos.domain.dto.UserIdDomain

class ProfilePagesAdapter(
    context: Context,
    fragmentManager: FragmentManager,
    private val userId: UserIdDomain,
    private val collapseListener: () -> Unit
) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val tabTitles = context.resources.getStringArray(R.array.profile_page_tab_titles)

    private var pagesList = mutableListOf(
        ProfilePostsFragment.newInstance(),
        ProfileCommentsFragment.newInstance(userId).apply {
            setCollapseListener(collapseListener)
        }
    )

    override fun getPageTitle(position: Int): CharSequence? = tabTitles.getOrNull(position)

    override fun getItem(position: Int): Fragment = pagesList[position]

    override fun getCount(): Int = pagesList.size
}
