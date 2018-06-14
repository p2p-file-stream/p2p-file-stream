package com.github.p2pfilestream.rendezvous.relay

/**
 * Collection that acts like a bi-directional HashMap.
 * Used to store unique pairs.
 */
class PairSet<T : Any> {
    private val hashMap = HashMap<T, T>()

    /**
     * Store two items as a pair.
     * Removes old mappings.
     */
    fun pair(a: T, b: T) {
        /** Map a to b, and remove the old mapping */
        hashMap.put(a, b)?.let(hashMap::remove)
        /** Map b to a, and remove the old mapping */
        hashMap.put(b, a)?.let(hashMap::remove)
    }

    /** Find other member of pair, or null */
    fun other(item: T): T? = hashMap[item]

    /**
     * Remove the pair this item belongs to.
     * @return other member in pair, or null if pair not exists
     */
    fun remove(item: T): T? {
        // Remove associations in both directions
        val other = hashMap.remove(item)
        if (other == null) {
            return null
        }
        hashMap.remove(other)
        return other
    }
}