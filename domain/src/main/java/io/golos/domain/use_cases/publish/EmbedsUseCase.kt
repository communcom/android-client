package io.golos.domain.use_cases.publish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.ProcessedLinksEntity
import io.golos.domain.repositories.Repository
import io.golos.domain.requestmodel.EmbedRequest
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.model.LinkEmbedModel
import io.golos.domain.use_cases.model.ProccesedLinksModel
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
class EmbedsUseCase
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val embedsRepository: Repository<ProcessedLinksEntity, EmbedRequest>
) : UseCase<ProccesedLinksModel> {

    private val processedLinks = MutableLiveData<ProccesedLinksModel>(ProccesedLinksModel(emptyMap()))

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    private var job: Job? = null

    override val getAsLiveData: LiveData<ProccesedLinksModel>
        get() = processedLinks

    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(embedsRepository.updateStates) {
            onRelatedDataChanges()
        }
        mediator.addSource(embedsRepository.getAsLiveData(embedsRepository.allDataRequest)) {

        }
    }

    private fun onRelatedDataChanges() {
        job?.cancel()
        job = repositoryScope.launch {

            delay(30)


            val resultState = withContext(dispatchersProvider.calculationsDispatcher) {

                val uploadResult = (
                        embedsRepository.getAsLiveData(embedsRepository.allDataRequest).value ?: ProcessedLinksEntity(
                            emptySet()
                        )).embeds.associateBy { it.originalQueryUrl }

                val updateStates = embedsRepository.updateStates
                    .value.orEmpty().values

                updateStates.associateBy { it.originalQuery.url }
                    .mapValues { mapEntry ->
                        val queryResult = mapEntry.value
                        when (queryResult) {
                            is QueryResult.Loading -> QueryResult.Loading(LinkEmbedModel.empty)
                            is QueryResult.Error -> QueryResult.Error(queryResult.error, LinkEmbedModel.empty)
                            is QueryResult.Success -> {
                                if (uploadResult.containsKey(queryResult.originalQuery.url)) {
                                    val result = uploadResult.getValue(queryResult.originalQuery.url)

                                    QueryResult.Success(
                                        LinkEmbedModel(
                                            result.summary, result.provider, result.url,
                                            result.thumbnailImageUrl, result.embeddedHtml, result.thumbnailSize
                                        )
                                    )
                                } else {
                                    System.err.println("for some reason, successful request is backed with result data")
                                    QueryResult.Error(
                                        IllegalStateException("for some reason, successful request is backed with result data"),
                                        LinkEmbedModel.empty
                                    )
                                }
                            }
                        }
                    }
            }

            processedLinks.value = ProccesedLinksModel(resultState)
        }
    }


    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(embedsRepository.updateStates)
        mediator.removeSource(embedsRepository.getAsLiveData(embedsRepository.allDataRequest))
    }
}