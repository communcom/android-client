package io.golos.cyber_android.ui.screens.community_page_about

import javax.inject.Inject

class CommunityPageAboutModelImpl @Inject constructor(private val description: String?):
    CommunityPageAboutModel {

    override fun getDescription(): String? = description
}