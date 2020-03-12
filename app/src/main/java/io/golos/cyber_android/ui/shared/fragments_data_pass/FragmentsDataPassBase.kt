package io.golos.cyber_android.ui.shared.fragments_data_pass

/**
 * Base class for passing data across fragments
 */
abstract class FragmentsDataPassBase {
    private val dataStorage: MutableMap<Int, Any> by lazy { mutableMapOf<Int, Any>() }

    protected fun put(key: Int, value: Any) {
        dataStorage.put(key, value)
    }

    /**
     * Gets data item from the storage
     */
    protected fun get(key: Int): Any? = dataStorage[key]

    /**
     * Gets data item from the storage and remove the item
     */
    protected fun remove(key: Int): Any? = dataStorage.remove(key)
}