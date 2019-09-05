package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.post_map

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.BlockType
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.links_repository.LinksRepository
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory.RenderersFactory
import java.lang.UnsupportedOperationException

/**
 * Render very bottom block - a map of the post
 */
@Suppress("SpellCheckingInspection")
class PostMapRenderer(
    private val builder: HtmlBuilder,
    private val renderersFactory: RenderersFactory
) {
    fun render(linksRepository: LinksRepository) {
        builder.putPostMap {
            linksRepository.getAllLinks().forEach { link ->
                renderersFactory.getRenderer(link).render(link)
            }
        }
    }
}