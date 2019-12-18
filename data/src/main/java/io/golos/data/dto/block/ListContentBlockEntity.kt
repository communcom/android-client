package io.golos.data.dto.block

import com.squareup.moshi.Json

data class ListContentBlockEntity(@Json(name = "content") val content: List<ContentBlockEntity>)