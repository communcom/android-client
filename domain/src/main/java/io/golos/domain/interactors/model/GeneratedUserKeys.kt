package io.golos.domain.interactors.model

import io.golos.cyber4j.model.CyberName
import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
data class GeneratedUserKeys(
    val userName: CyberName,
    val masterPassword: String,
    val ownerKey: String,
    val activeKey: String,
    val postingKey: String,
    val memoKey: String
) : Model