package io.golos.domain.interactors.model

import android.os.Parcelable
import io.golos.domain.Model
import io.golos.domain.commun_entities.CommunityId
import kotlinx.android.parcel.Parcelize

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
@Parcelize
data class CommunityModel(
    val id: CommunityId,
    val name: String,
    val avatarUrl: String?
) : Model, Parcelable