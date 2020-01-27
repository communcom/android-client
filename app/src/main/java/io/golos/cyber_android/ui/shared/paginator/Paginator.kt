package io.golos.cyber_android.ui.shared.paginator

import timber.log.Timber
import javax.inject.Inject

object Paginator {

    sealed class State {
        object Empty : State()
        object EmptyProgress : State()
        data class EmptyError(val error: Throwable) : State()
        data class Data<T>(val pageCount: Int, val data: List<T>, val pageKey: String? = null) : State()
        data class Refresh<T>(val pageCount: Int, val data: List<T>, val pageKey: String? = null) : State()
        data class NewPageProgress<T>(val pageCount: Int, val data: List<T>, val pageKey: String? = null) : State()
        data class SearchProgress<T>(val pageCount: Int, val data: List<T>, val pageKey: String? = null) : State()
        data class FullData<T>(val pageCount: Int, val data: List<T>, val pageKey: String? = null) : State()
        data class PageError<T>(val pageCount: Int, val data: List<T>, val pageKey: String? = null) : State()
        data class SearchPageError<T>(val pageCount: Int, val data: List<T>, val pageKey: String? = null) : State()
    }

    sealed class Action {
        object Refresh : Action()
        object Restart : Action()
        object LoadMore : Action()
        data class NewPage<T>(val pageCount: Int, val items: List<T>, val pageKey: String? = null) : Action()
        data class PageError(val error: Throwable) : Action()
        object Search : Action()
    }

    sealed class SideEffect {
        data class LoadPage(val pageCount: Int, val pageKey: String? = null) : SideEffect()
        data class ErrorEvent(val error: Throwable) : SideEffect()
    }

    private fun <T> reducer(
        action: Action,
        state: State,
        sideEffectListener: (SideEffect) -> Unit
    ): State =
        when (action) {
            is Action.Refresh -> {
                sideEffectListener(SideEffect.LoadPage(0))
                when (state) {
                    is State.Empty -> State.EmptyProgress
                    is State.EmptyError -> State.EmptyProgress
                    is State.Data<*> -> State.Refresh(state.pageCount, state.data as List<T>, state.pageKey)
                    is State.NewPageProgress<*> -> State.Refresh(
                        state.pageCount,
                        state.data as List<T>,
                        state.pageKey
                    )
                    is State.FullData<*> -> State.Refresh(state.pageCount, state.data as List<T>, state.pageKey)
                    else -> state
                }
            }
            is Action.Restart -> {
                sideEffectListener(SideEffect.LoadPage(0))
                when (state) {
                    is State.Empty -> State.EmptyProgress
                    is State.EmptyError -> State.EmptyProgress
                    is State.Data<*> -> State.EmptyProgress
                    is State.Refresh<*> -> State.EmptyProgress
                    is State.NewPageProgress<*> -> State.EmptyProgress
                    is State.FullData<*> -> State.EmptyProgress
                    else -> state
                }
            }
            is Action.Search -> {
                sideEffectListener(SideEffect.LoadPage(0))
                when (state) {
                    is State.Empty -> State.EmptyProgress
                    is State.EmptyError -> State.EmptyProgress
                    is State.Data<*> -> {
                        State.SearchProgress(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    is State.Refresh<*> -> State.EmptyProgress
                    is State.NewPageProgress<*> -> State.EmptyProgress
                    is State.FullData<*> -> {
                        State.SearchProgress(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    is State.PageError<*> -> {
                        State.SearchProgress(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    is State.SearchPageError<*> -> {
                        State.SearchProgress(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    else -> state
                }
            }
            is Action.LoadMore -> {
                when (state) {
                    is State.Data<*> -> {
                        Timber.d("paginator:[DATA]")
                        sideEffectListener(SideEffect.LoadPage(state.pageCount + 1))
                        State.NewPageProgress(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    is State.PageError<*> -> {
                        Timber.d("paginator:[PAGE_ERROR]")
                        sideEffectListener(SideEffect.LoadPage(state.pageCount + 1))
                        State.NewPageProgress(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    is State.SearchPageError<*> -> {
                        Timber.d("paginator:[SEARCH_PAGE_ERROR]")
                        sideEffectListener(SideEffect.LoadPage(state.pageCount))
                        State.NewPageProgress(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    else -> state
                }
            }
            is Action.NewPage<*> -> {
                Timber.d("paginator:[NEW_PAGE]")
                val items = action.items as List<T>
                val pageKey = action.pageKey
                Timber.d("paginator: items size -> ${items.size}")
                when (state) {
                    is State.EmptyProgress -> {
                        Timber.d("paginator:[EMPTY_PROGRESS]")
                        if (items.isEmpty()) {
                            State.Empty
                        } else {
                            State.Data(0, items, pageKey)
                        }
                    }
                    is State.Refresh<*> -> {
                        Timber.d("paginator:[REFRESH]")
                        if (items.isEmpty()) {
                            State.Empty
                        } else {
                            State.Data(0, items, pageKey)
                        }
                    }
                    is State.NewPageProgress<*> -> {
                        Timber.d("paginator:[NEW_PAGE_PROGRESS]")
                        if (items.isEmpty()) {
                            Timber.d("paginator: items is empty")
                            State.FullData(state.pageCount, state.data as List<T>, pageKey)
                        } else {
                            Timber.d("paginator: set -> [STATE.DATA]")
                            State.Data(state.pageCount + 1, state.data as List<T> + items, pageKey)
                        }
                    }
                    is State.SearchProgress<*> -> {
                        if (items.isEmpty()) {
                            State.FullData(0, emptyList<T>(), pageKey)
                        } else {
                            State.Data(0, items, pageKey)
                        }
                    }
                    else -> state
                }
            }
            is Action.PageError -> {
                when (state) {
                    is State.EmptyProgress -> State.EmptyError(action.error)
                    is State.Refresh<*> -> {
                        sideEffectListener(SideEffect.ErrorEvent(action.error))
                        State.Data(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    is State.NewPageProgress<*> -> {
                        sideEffectListener(SideEffect.ErrorEvent(action.error))
                        State.PageError(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    is State.SearchProgress<*> -> {
                        sideEffectListener(SideEffect.ErrorEvent(action.error))
                        State.SearchPageError(state.pageCount, state.data as List<T>, state.pageKey)
                    }
                    else -> state
                }
            }
        }

    class Store<T> @Inject constructor() {
        private var state: State = State.Empty

        fun initState(state: State) {
            this.state = state
        }

        var render: (State) -> Unit = {}
            set(value) {
                field = value
                value(state)
            }

        var sideEffectListener: (SideEffect) -> Unit = {
            when (it) {
                is SideEffect.LoadPage -> {
                    Timber.d("paginator: SideEffect.LoadPage")
                }
                is SideEffect.ErrorEvent -> {
                    Timber.d("paginator: SideEffect.ErrorEvent")
                }
            }
        }

        fun proceed(action: Action) {
            Timber.d("paginator: [Action: ${action::class.java.simpleName.toUpperCase()}]")
            val newState = reducer<T>(action, state) { sideEffect ->
                sideEffectListener.invoke(sideEffect)
            }
            Timber.d("paginator: current state -> [${state::class.java.simpleName.toUpperCase()}]")
            Timber.d("paginator: new state -> [${newState::class.java.simpleName.toUpperCase()}]")
            if (newState != state) {
                state = newState
                Timber.d("[New state: $state]")
                render.invoke(state)
            }
        }

        fun getStoredItems(): List<T> {
            return when (val currentState: State = state) {
                is State.Empty -> emptyList()
                is State.EmptyProgress -> emptyList()
                is State.EmptyError -> emptyList()
                is State.Data<*> -> currentState.data as List<T>
                is State.Refresh<*> -> currentState.data as List<T>
                is State.NewPageProgress<*> -> currentState.data as List<T>
                is State.SearchProgress<*> -> currentState.data as List<T>
                is State.FullData<*> -> currentState.data as List<T>
                is State.PageError<*> -> currentState.data as List<T>
                is State.SearchPageError<*> -> currentState.data as List<T>
            }
        }

        fun updateStoredItems(items: List<T>) {
            state = when (val currentState: State = state) {
                is State.Data<*> -> Paginator.State.Data(currentState.pageCount, items, currentState.pageKey)
                is State.Refresh<*> -> Paginator.State.Refresh(currentState.pageCount, items, currentState.pageKey)
                is State.NewPageProgress<*> -> Paginator.State.NewPageProgress(currentState.pageCount, items, currentState.pageKey)
                is State.SearchProgress<*> -> Paginator.State.SearchProgress(currentState.pageCount, items, currentState.pageKey)
                is State.FullData<*> -> Paginator.State.FullData(currentState.pageCount, items, currentState.pageKey)
                is State.PageError<*> -> Paginator.State.PageError(currentState.pageCount, items, currentState.pageKey)
                is State.SearchPageError<*> -> Paginator.State.SearchPageError(currentState.pageCount, items, currentState.pageKey)
                else -> currentState
            }
        }
    }
}
