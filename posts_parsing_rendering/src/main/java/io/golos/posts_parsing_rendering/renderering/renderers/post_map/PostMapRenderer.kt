package io.golos.posts_parsing_rendering.renderering.renderers.post_map

import io.golos.posts_parsing_rendering.renderering.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.renderering.links_repository.LinksRepository
import io.golos.posts_parsing_rendering.renderering.renderers_factory.RenderersFactory

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