package io.golos.cyber_android.ui.common.fragments_data_pass

/**
 * Base class for passing data across fragments
 */
abstract class FragmentsDataPassBase {
    private val dataStorage: MutableMap<Int, Any> by lazy { mutableMapOf<Int, Any>() }

    protected fun put(key: Int, value: Any) {
        dataStorage.put(key, value)
    }

    /**
     * Gets data item from the storage end remove the item
     */
    protected fun get(key: Int): Any? = dataStorage.remove(key)
}