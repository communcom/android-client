package io.golos.domain.interactors.model

import io.golos.domain.Model
import io.golos.domain.entities.CyberUser

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
data class DiscussionAuthorModel(val userId: CyberUser, val username: String, val avatarUrl: String) : Model