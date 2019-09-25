package io.golos.posts_parsing_rendering.json_to_html.renderers.post_map

import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.json_to_html.links_repository.LinksRepository
import io.golos.posts_parsing_rendering.json_to_html.renderers_factory.RenderersFactory

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