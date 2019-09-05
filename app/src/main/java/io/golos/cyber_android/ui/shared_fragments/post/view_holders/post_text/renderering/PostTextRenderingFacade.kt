package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering

import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.links_repository.LinksRepository
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.*
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.exceptions.ExceptionRenderer
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.post_map.PostMapRenderer
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory.RenderersFactory
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory.RenderersMessageFactory
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory.RenderersPostMapFactory
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

@Suppress("SpellCheckingInspection")
class PostTextRenderingFacade
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val appResources: AppResourcesProvider
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
                App.logger.log(ex)
                ExceptionRenderer(appResources, builder).render(ex)
            }

            builder.build()
        }
}