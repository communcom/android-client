package io.golos.posts_parsing_rendering.json_to_dto.mappers

import kotlin.reflect.KClass

class MappersFactory {
    private val mappers = mutableMapOf<KClass<*>, MapperBase<*>>()

    @Suppress("UNCHECKED_CAST")
    fun <T: MapperBase<*>>getMapper(type: KClass<*>): T {
        val mapper = mappers[type]
        if(mapper != null) {
            return mapper as T
        }

        return when(type) {
            PostMapper::class -> PostMapper(this)

            ParagraphMapper::class -> ParagraphMapper(this)

            WebsiteMapper::class -> WebsiteMapper(this)
            VideoMapper::class -> VideoMapper(this)
            ImageMapper::class -> ImageMapper(this)

            TextMapper::class -> TextMapper(this)
            TagMapper::class -> TagMapper(this)
            MentionMapper::class -> MentionMapper(this)
            LinkMapper::class -> LinkMapper(this)

            AttachmentsMapper::class -> AttachmentsMapper(this)

            else -> throw UnsupportedOperationException("This type of block is not supported: $type")
        }
        .also { mappers[type] = it } as T
    }
}