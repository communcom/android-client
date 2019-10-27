package io.golos.cyber_android.ui.screens.community_page_about

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase

interface CommunityPageAboutModel : ModelBase{

    fun getDescription(): String?
}