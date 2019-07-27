package csense.idea.kotlin.checked.exceptions.cache

import csense.kotlin.extensions.*
import java.util.*
import kotlin.also
import kotlin.collections.HashMap

//TODO csense-datastructures ?
class SimpleLRUCache<Key, Value>(private val cacheSize: Int) {

    //TODO consider linked hashmap, potentially own edition where we can query the order since this is a "borring" replica of that.
    private val map = HashMap<Key, Value>(cacheSize)

    private val order = LinkedList<Key>()

    /**
     *
     * @param key Key
     * @param value Value
     * @return Key?
     * @timecomplexity O(1)
     */
    fun put(key: Key, value: Value): Key? {

        val evictedKey: Key? = shouldEvict().mapLazy(
                ifTrue = { evict() },
                ifFalse = { null })

        map[key] = value
        order.addLast(key)
        return evictedKey
    }

    fun containsKey(key: Key) = map.containsKey(key)

    fun notContainsKey(key: Key) = !containsKey(key)

    /**
     *
     * @return Boolean true if we are at max size thus we have to evict.
     */
    private fun shouldEvict(): Boolean = map.size >= cacheSize

    /**
     *
     * @return Key?
     * @timecomplexity O(1)
     */
    private fun evict(): Key? = getKeyToEvict().also {
        map.remove(it)
    }

    /**
     *
     * @param key Key
     * @return Value?
     * @timecomplexity O(1)
     */
    operator fun get(key: Key): Value? = map[key]

    /**
     * Gets a given value , and if there and the given condition is met the value is returned,
     * if the condition is not met, the item is evicted and null is returned.
     * @return value?
     * @timecomplexity O(1) to O(n) if condition is false, n = size of data
     */
    fun getOrRemove(key: Key, condition: Function2<Key, Value, Boolean>): Value? {
        val value = get(key) ?: return null
        return if (condition(key, value)) {
            value
        } else {
            remove(key);
            null
        }
    }

    /**
     *
     * @return Key?
     * @timecomplexity O(1)
     */
    private fun getKeyToEvict(): Key? = order.removeFirst()

    /**
     *
     * @param key Key
     * @timecomplexity O(n) where n = size of data
     */
    fun remove(key: Key) {
        map.remove(key)
        order.remove(key)
    }
}