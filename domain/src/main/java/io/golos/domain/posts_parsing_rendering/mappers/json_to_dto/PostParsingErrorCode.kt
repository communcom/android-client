package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto

enum class PostParsingErrorCode {
    /**
     * Version of a json document is too high
     */
    INCOMPATIBLE_VERSIONS,

    /**
     * Invalid json format
     */
    JSON,

    GENERAL
}