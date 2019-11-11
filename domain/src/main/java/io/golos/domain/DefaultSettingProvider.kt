package io.golos.domain

import io.golos.domain.dto.UserSettingEntity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
interface DefaultSettingProvider {
    fun provide(): UserSettingEntity
}