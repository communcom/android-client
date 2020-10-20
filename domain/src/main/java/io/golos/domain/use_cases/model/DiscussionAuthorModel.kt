package io.golos.domain.use_cases.model

import io.golos.domain.Model
import io.golos.domain.dto.CyberUser

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
data class DiscussionAuthorModel(val userId: CyberUser, val username: String, val avatarUrl: String?) : Model