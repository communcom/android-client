package io.golos.cyber_android.ui.common.paginator

import timber.log.Timber
import javax.inject.Inject

object Paginator {

    sealed class State {
        object Empty : State()
        object EmptyProgress : State()
        data class EmptyError(val error: Throwable) : State()
        data class Data<T>(val pageCount: Int, val data: List<T>) : State()
        data class Refresh<T>(val pageCount: Int, val data: List<T>) : State()
        data class NewPageProgress<T>(val pageCount: Int, val data: List<T>) : State()
        data class SearchProgress<T>(val pageCount: Int, val data: List<T>) : State()
        data class FullData<T>(val pageCount: Int, val data: List<T>) : State()
        data class PageError<T>(val pageCount: Int, val data: List<T>) : State()
        data class SearchPageError<T>(val pageCount: Int, val data: List<T>) : State()
    }

    sealed class Action {
        object Refresh : Action()
        object Restart : Action()
        object LoadMore : Action()
        data class NewPage<T>(val pageCount: Int, val items: List<T>) : Action()
        data class PageError(val error: Throwable) : Action()
        object Search : Action()
    }

    sealed class SideEffect {
        data class LoadPage(val pageCount: Int) : SideEffect()
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
                    is State.Data<*> -> State.Refresh(state.pageCount, state.data as List<T>)
                    is State.NewPageProgress<*> -> State.Refresh(
                        state.pageCount,
                        state.data as List<T>
                    )
                    is State.FullData<*> -> State.Refresh(state.pageCount, state.data as List<T>)
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
                        State.SearchProgress(state.pageCount, state.data as List<T>)
                    }
                    is State.Refresh<*> -> State.EmptyProgress
                    is State.NewPageProgress<*> -> State.EmptyProgress
                    is State.FullData<*> -> {
                        State.SearchProgress(state.pageCount, state.data as List<T>)
                    }
                    is State.PageError<*> -> {
                        State.SearchProgress(state.pageCount, state.data as List<T>)
                    }
                    is State.SearchPageError<*> -> {
                        State.SearchProgress(state.pageCount, state.data as List<T>)
                    }
                    else -> state
                }
            }
            is Action.LoadMore -> {
                when (state) {
                    is State.Data<*> -> {
                        sideEffectListener(SideEffect.LoadPage(state.pageCount + 1))
                        State.NewPageProgress(state.pageCount, state.data as List<T>)
                    }
                    is State.PageError<*> -> {
                        sideEffectListener(SideEffect.LoadPage(state.pageCount + 1))
                        State.NewPageProgress(state.pageCount, state.data as List<T>)
                    }
                    is State.SearchPageError<*> -> {
                        sideEffectListener(SideEffect.LoadPage(state.pageCount))
                        State.NewPageProgress(state.pageCount, state.data as List<T>)
                    }
                    else -> state
                }
            }
            is Action.NewPage<*> -> {
                val items = action.items as List<T>
                when (state) {
                    is State.EmptyProgress -> {
                        if (items.isEmpty()) {
                            State.Empty
                        } else {
                            State.Data(0, items)
                        }
                    }
                    is State.Refresh<*> -> {
                        if (items.isEmpty()) {
                            State.Empty
                        } else {
                            State.Data(0, items)
                        }
                    }
                    is State.NewPageProgress<*> -> {
                        if (items.isEmpty()) {
                            State.FullData(state.pageCount, state.data as List<T>)
                        } else {
                            State.Data(state.pageCount + 1, state.data as List<T> + items)
                        }
                    }
                    is State.SearchProgress<*> -> {
                        if (items.isEmpty()) {
                            State.FullData(state.pageCount, emptyList<T>())
                        } else {
                            State.Data(state.pageCount + 1, items)
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
                        State.Data(state.pageCount, state.data as List<T>)
                    }
                    is State.NewPageProgress<*> -> {
                        sideEffectListener(SideEffect.ErrorEvent(action.error))
                        State.PageError(state.pageCount, state.data as List<T>)
                    }
                    is State.SearchProgress<*> -> {
                        sideEffectListener(SideEffect.ErrorEvent(action.error))
                        State.SearchPageError(state.pageCount, state.data as List<T>)
                    }
                    else -> state
                }
            }
        }

    class Store<T> @Inject constructor() {
        private var state: State = State.Empty

        fun initState(state: State){
            this.state = state
        }

        var render: (State) -> Unit = {}
            set(value) {
                field = value
                value(state)
            }

        var sideEffectListener: (SideEffect) -> Unit = {
            when(it){
                is SideEffect.LoadPage -> {}
                is SideEffect.ErrorEvent -> {}
            }
        }

        fun proceed(action: Action) {
            Timber.d("Action: $action")
            val newState = reducer<T>(action, state) { sideEffect ->
                sideEffectListener.invoke(sideEffect)
            }
            if (newState != state) {
                state = newState
                Timber.d("New state: $state")
                render(state)
            }
        }
    }
}
