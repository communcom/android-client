package io.golos.posts_parsing_rendering.json_to_html.renderers_factory

import io.golos.posts_parsing_rendering.json_to_html.renderers.RendererBase
import org.json.JSONObject

@Suppress("SpellCheckingInspection")
interface RenderersFactory {
    fun getRenderer(block: JSONObject): RendererBase
}