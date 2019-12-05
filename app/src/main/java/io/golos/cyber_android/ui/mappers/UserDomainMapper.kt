package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.User
import io.golos.domain.dto.UserDomain

fun UserDomain.mapToUser(): User{
    return User(
        this.userId.userId,
        this.userName,
        this.userAvatar.orEmpty()
    )
}