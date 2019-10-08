package io.golos.cyber_android.ui.common.mvvm.paginator

import timber.log.Timber
import javax.inject.Inject

object Paginator {

    sealed class State {
        object Empty : State()
        object EmptyProgress : State()
        data class EmptyError(val error: Throwable) : State()
        data class Data<T>(val sequenceKey: String?, val data: List<T>) : State()
        data class Refresh<T>(val sequenceKey: String?, val data: List<T>) : State()
        data class NewPageProgress<T>(val sequenceKey: String?, val data: List<T>) : State()
        data class FullData<T>(val sequenceKey: String?, val data: List<T>) : State()
    }

    sealed class Action {
        object Refresh : Action()
        object Restart : Action()
        object LoadMore : Action()
        data class NewPage<T>(val sequenceKey: String?, val items: List<T>) : Action()
        data class PageError(val error: Throwable) : Action()
    }

    sealed class SideEffect {
        data class LoadPage(val sequenceKey: String?) : SideEffect()
        data class ErrorEvent(val error: Throwable) : SideEffect()
    }

    private fun <T> reducer(
        action: Action,
        state: State,
        sideEffectListener: (SideEffect) -> Unit
    ): State =
        when (action) {
            is Action.Refresh -> {
                sideEffectListener(SideEffect.LoadPage(null))
                when (state) {
                    is State.Empty -> State.EmptyProgress
                    is State.EmptyError -> State.EmptyProgress
                    is State.Data<*> -> State.Refresh(state.sequenceKey, state.data as List<T>)
                    is State.NewPageProgress<*> -> State.Refresh(
                        state.sequenceKey,
                        state.data as List<T>
                    )
                    is State.FullData<*> -> State.Refresh(state.sequenceKey, state.data as List<T>)
                    else -> state
                }
            }
            is Action.Restart -> {
                sideEffectListener(SideEffect.LoadPage(null))
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
            is Action.LoadMore -> {
                when (state) {
                    is State.Data<*> -> {
                        sideEffectListener(SideEffect.LoadPage(state.sequenceKey))
                        State.NewPageProgress(state.sequenceKey, state.data as List<T>)
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
                            State.Data(null, items)
                        }
                    }
                    is State.Refresh<*> -> {
                        if (items.isEmpty()) {
                            State.Empty
                        } else {
                            State.Data(null, items)
                        }
                    }
                    is State.NewPageProgress<*> -> {
                        if (items.isEmpty()) {
                            State.FullData(state.sequenceKey, state.data as List<T>)
                        } else {
                            State.Data(state.sequenceKey, state.data as List<T> + items)
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
                        State.Data(state.sequenceKey, state.data as List<T>)
                    }
                    is State.NewPageProgress<*> -> {
                        sideEffectListener(SideEffect.ErrorEvent(action.error))
                        State.Data(state.sequenceKey, state.data as List<T>)
                    }
                    else -> state
                }
            }
        }

    class Store<T> @Inject constructor() {
        private var state: State = State.Empty
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
                sideEffectListener?.invoke(sideEffect)
            }
            if (newState != state) {
                state = newState
                Timber.d("New state: $state")
                render(state)
            }
        }
    }
}
