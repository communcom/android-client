package io.golos.domain.posts_parsing_rendering.mappers.comment_to_json.tags

/**
 * Temporary algorithm for extracting tags from a string
 */
object CommentTagsExtractor {
    data class StringPart(
        val text: String,
        val isTag: Boolean
    )

    fun extract(text: String): List<StringPart> {
        val result = mutableListOf<StringPart>()

        val parts = text.split("#")

        val startIndex = if(text.startsWith("#")) 0 else 1

        if(startIndex == 1) {
            result.add(
                StringPart(
                    parts[0],
                    false
                )
            )
        }

        for(i in startIndex until parts.size) {
            if(parts[i].isNotEmpty()) {
                splitString(
                    parts[i],
                    result
                )
            }
        }

        return result
    }

    private fun splitString(s: String, partsList: MutableList<StringPart>) {
        s.indexOf(" ")
            .let {
                if(it == -1) {
                    partsList.add(
                        StringPart(
                            s,
                            true
                        )
                    )
                } else {
                    partsList.add(
                        StringPart(
                            s.substring(0 until it),
                            true
                        )
                    )
                    partsList.add(
                        StringPart(
                            s.substring(it),
                            false
                        )
                    )
                }
            }
    }
}