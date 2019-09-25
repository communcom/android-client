package io.golos.posts_parsing_rendering.json_to_html.renderers_factory

import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.json_to_html.renderers.RendererBase
import org.json.JSONObject

@Suppress("SpellCheckingInspection")
abstract class RenderersFactoryBase(protected val builder: HtmlBuilder): RenderersFactory {

    private val renderers: MutableMap<String, RendererBase> = mutableMapOf()

    override fun getRenderer(block: JSONObject): RendererBase {
        val type = block.getString("type")

        val renderer = renderers[type]

        return if(renderer != null) {
            renderer
        } else {
            val newRenderer =  createRenderer(type)
            renderers[type] = newRenderer
            newRenderer
        }
    }

    protected abstract fun createRenderer(type: String): RendererBase
}