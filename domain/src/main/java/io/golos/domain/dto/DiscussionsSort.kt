package io.golos.domain.dto

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
enum class DiscussionsSort: Entity {
    FROM_NEW_TO_OLD,
    FROM_OLD_TO_NEW,
    POPULAR
}