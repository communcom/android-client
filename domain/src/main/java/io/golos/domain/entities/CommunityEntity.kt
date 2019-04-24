package io.golos.domain.entities

import io.golos.cyber4j.model.CyberCommunity
import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
data class CommunityEntity(
    val id: String,
    val name: String,
    val avatarUrl: String?
) : Entity {
    companion object {
        fun fromCyber(cyberObject: CyberCommunity) =
            CommunityEntity(cyberObject.id, cyberObject.name, cyberObject.getAvatarUrl)
    }
}

