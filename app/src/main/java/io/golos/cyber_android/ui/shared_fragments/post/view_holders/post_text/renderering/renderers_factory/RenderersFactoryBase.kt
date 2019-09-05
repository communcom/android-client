package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.RendererBase
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