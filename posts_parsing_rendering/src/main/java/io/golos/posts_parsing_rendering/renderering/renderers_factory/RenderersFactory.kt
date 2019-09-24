package io.golos.posts_parsing_rendering.renderering.renderers_factory

import io.golos.posts_parsing_rendering.renderering.renderers.RendererBase
import org.json.JSONObject

@Suppress("SpellCheckingInspection")
interface RenderersFactory {
    fun getRenderer(block: JSONObject): RendererBase
}