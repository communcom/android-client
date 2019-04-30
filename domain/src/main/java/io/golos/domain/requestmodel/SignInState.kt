package io.golos.domain.requestmodel

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */

enum class SignInState : Model {
    LOG_IN_NEEDED, USER_LOGGED_IN, LOADING
}