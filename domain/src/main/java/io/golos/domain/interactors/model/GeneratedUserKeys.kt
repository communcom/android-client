package io.golos.domain.interactors.model

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
data class GeneratedUserKeys(
    val userName: String,
    val masterPassword: String,
    val ownerPublicKey: String,
    val ownerPrivateKey: String,
    val activePublicKey: String,
    val activePrivateKey: String,
    val postingPublicKey: String,
    val postingPrivateKey: String,
    val memoPublicKey: String,
    val memoPrivateKey: String
) : Model