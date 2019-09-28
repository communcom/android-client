package io.golos.posts_parsing_rendering

enum class BlockType(val value: String) {
    POST("post"),
    PARAGRAPH("paragraph"),
    TEXT("text"),
    TAG("tag"),
    MENTION("mention"),
    LINK("link"),
    IMAGE("image"),
    VIDEO("video"),
    WEBSITE("website"),
    ATTACHMENTS("attachments")
}

enum class Attribute(val value: String) {
    VERSION("version"),
    TITLE("title"),
    STYLE("style"),
    TEXT_COLOR("text_color"),
    URL("url"),
    TYPE("type"),
    THUMBNAIL_URL("thumbnail_url"),
    THUMBNAIL_SIZE("thumbnail_size"),
    DESCRIPTION("description"),
    PROVIDER_NAME("provider_name"),
    HTML("html"),
    AUTHOR("author"),
    AUTHOR_URL("author_url")
}

object LinkTypeJson {
    const val IMAGE = "image"
    const val VIDEO = "video"
    const val WEBSITE = "website"
}