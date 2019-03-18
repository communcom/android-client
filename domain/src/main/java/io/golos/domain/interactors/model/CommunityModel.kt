package io.golos.domain.interactors.model

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
data class CommunityModel(
    val id: String,
    val name: String,
    val avatarUrl: String?
) : Model