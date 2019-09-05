package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.links_repository

import org.json.JSONObject

/**
 * Collection of links in a post
 */
class LinksRepository {
    private val links: MutableList<JSONObject> = mutableListOf()

    fun putLink(value: JSONObject) = links.add(value)

    fun getAllLinks(): List<JSONObject> = links.toList()
}