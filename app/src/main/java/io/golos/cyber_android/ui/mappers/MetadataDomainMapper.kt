package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Meta
import io.golos.domain.dto.MetaDomain

fun MetaDomain.mapToMeta(): Meta {
    return Meta(this.creationTime,this.trxId)
}