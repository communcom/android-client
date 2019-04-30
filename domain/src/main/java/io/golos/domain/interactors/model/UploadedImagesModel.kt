package io.golos.domain.interactors.model

import io.golos.domain.Model
import io.golos.domain.requestmodel.QueryResult


/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-29.
 */
inline class UploadedImageModel(val url: String) : Model {
    companion object {
        val empty = UploadedImageModel("")
    }
}

data class UploadedImagesModel(val map: Map<String, QueryResult<UploadedImageModel>>) :
    Map<String, QueryResult<UploadedImageModel>> by map, Model