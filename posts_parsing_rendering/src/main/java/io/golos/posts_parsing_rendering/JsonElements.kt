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
    WEBSITE("website")
}

enum class Attribute(val value: String) {
    VERSION("version"),
    TITLE("title"),
    STYLE("style"),
    TEXT_COLOR("text_color"),
    URL("url"),
    TYPE("type"),
    THUMBNAIL_URL("thumbnail_url"),
    DESCRIPTION("description"),
    PROVIDER_NAME("provider_name"),
    HTML("html")
}