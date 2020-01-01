package io.golos.cyber_android.ui.screens.community_page_about

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface CommunityPageAboutModel : ModelBase{

    fun getDescription(): String?
}