package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.User
import io.golos.domain.dto.UserDomain

class UserDomainToUserMapper : Function1<UserDomain, User> {
    override fun invoke(userProfileDomain: UserDomain): User {
        return User(userProfileDomain.userId,
            userProfileDomain.userName,
            userProfileDomain.userAvatar)
    }
}