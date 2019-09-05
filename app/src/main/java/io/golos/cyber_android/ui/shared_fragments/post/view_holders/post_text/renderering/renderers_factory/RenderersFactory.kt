package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.RendererBase
import org.json.JSONObject

@Suppress("SpellCheckingInspection")
interface RenderersFactory {
    fun getRenderer(block: JSONObject): RendererBase
}