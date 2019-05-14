package io.golos.domain.entities

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-29.
 */
inline class UploadedImageEntity(val url: String) : Entity

data class UploadedImagesEntity(val imageUrls: Map<String, UploadedImageEntity>) : Entity,
    Map<String, UploadedImageEntity> by imageUrls