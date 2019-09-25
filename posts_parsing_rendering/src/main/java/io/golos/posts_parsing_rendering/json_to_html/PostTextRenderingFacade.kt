package io.golos.posts_parsing_rendering.json_to_html

import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.json_to_html.links_repository.LinksRepository
import io.golos.posts_parsing_rendering.json_to_html.renderers.exceptions.ExceptionRenderer
import io.golos.posts_parsing_rendering.json_to_html.renderers.post_map.PostMapRenderer
import io.golos.posts_parsing_rendering.json_to_html.renderers_factory.RenderersMessageFactory
import io.golos.posts_parsing_rendering.json_to_html.renderers_factory.RenderersPostMapFactory
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import kotlinx.coroutines.withContext
import org.json.JSONObject

@Suppress("SpellCheckingInspection")
class PostTextRenderingFacade
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val appResources: AppResourcesProvider,
    private val logger: Logger
) {
    private val builder: HtmlBuilder by lazy { HtmlBuilder() }
    private val linksRepository: LinksRepository by lazy { LinksRepository() }

    /**
     * Renders post to Html
     */
    suspend fun render(rawPost: String): String =
        withContext(dispatchersProvider.calculationsDispatcher) {
            try {
                JSONObject(rawPost)
                    .let {
                        RenderersMessageFactory(builder, linksRepository)
                            .getRenderer(it)       // Starts rendering from a root block (post)
                            .render(it)
                    }

                // Renders post map here
                PostMapRenderer(builder, RenderersPostMapFactory(builder)).render(linksRepository)
            } catch (ex: Exception) {
                logger.log(ex)
                ExceptionRenderer(appResources, builder).render(ex)
            }

            builder.build()
        }
}