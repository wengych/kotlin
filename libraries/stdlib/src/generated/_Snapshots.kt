package kotlin

//
// NOTE THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import kotlin.platform.*
import java.util.*

import java.util.Collections // TODO: it's temporary while we have java.util.Collections in js

/**
 * Returns an ArrayList of all elements
 */
public fun <T> Array<out T>.toArrayList(): ArrayList<T> {
    return this.asList().toArrayList()
}

/**
 * Returns an ArrayList of all elements
 */
public fun BooleanArray.toArrayList(): ArrayList<Boolean> {
    val list = ArrayList<Boolean>(size())
    for (item in this) list.add(item)
    return list
}

/**
 * Returns an ArrayList of all elements
 */
public fun ByteArray.toArrayList(): ArrayList<Byte> {
    val list = ArrayList<Byte>(size())
    for (item in this) list.add(item)
    return list
}

/**
 * Returns an ArrayList of all elements
 */
public fun CharArray.toArrayList(): ArrayList<Char> {
    val list = ArrayList<Char>(size())
    for (item in this) list.add(item)
    return list
}

/**
 * Returns an ArrayList of all elements
 */
public fun DoubleArray.toArrayList(): ArrayList<Double> {
    val list = ArrayList<Double>(size())
    for (item in this) list.add(item)
    return list
}

/**
 * Returns an ArrayList of all elements
 */
public fun FloatArray.toArrayList(): ArrayList<Float> {
    val list = ArrayList<Float>(size())
    for (item in this) list.add(item)
    return list
}

/**
 * Returns an ArrayList of all elements
 */
public fun IntArray.toArrayList(): ArrayList<Int> {
    val list = ArrayList<Int>(size())
    for (item in this) list.add(item)
    return list
}

/**
 * Returns an ArrayList of all elements
 */
public fun LongArray.toArrayList(): ArrayList<Long> {
    val list = ArrayList<Long>(size())
    for (item in this) list.add(item)
    return list
}

/**
 * Returns an ArrayList of all elements
 */
public fun ShortArray.toArrayList(): ArrayList<Short> {
    val list = ArrayList<Short>(size())
    for (item in this) list.add(item)
    return list
}

/**
 * Returns an ArrayList of all elements
 */
public fun <T> Collection<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

/**
 * Returns an ArrayList of all elements
 */
public fun <T> Iterable<T>.toArrayList(): ArrayList<T> {
    if (this is Collection<T>)
        return this.toArrayList()
    return toCollection(ArrayList<T>())
}

/**
 * Returns an ArrayList of all elements
 */
public fun <T> Sequence<T>.toArrayList(): ArrayList<T> {
    return toCollection(ArrayList<T>())
}


deprecated("Migrate to using Sequence<T> and respective functions")
/**
 * Returns an ArrayList of all elements
 */
public fun <T> Stream<T>.toArrayList(): ArrayList<T> {
    return toCollection(ArrayList<T>())
}

/**
 * Returns an ArrayList of all elements
 */
public fun String.toArrayList(): ArrayList<Char> {
    return toCollection(ArrayList<Char>(length()))
}

/**
 * Appends all elements to the given *collection*
 */
public fun <T, C : MutableCollection<in T>> Array<out T>.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <C : MutableCollection<in Boolean>> BooleanArray.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <C : MutableCollection<in Byte>> ByteArray.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <C : MutableCollection<in Char>> CharArray.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <C : MutableCollection<in Double>> DoubleArray.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <C : MutableCollection<in Float>> FloatArray.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <C : MutableCollection<in Int>> IntArray.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <C : MutableCollection<in Long>> LongArray.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <C : MutableCollection<in Short>> ShortArray.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <T, C : MutableCollection<in T>> Iterable<T>.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <T, C : MutableCollection<in T>> Sequence<T>.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}


deprecated("Migrate to using Sequence<T> and respective functions")
/**
 * Appends all elements to the given *collection*
 */
public fun <T, C : MutableCollection<in T>> Stream<T>.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Appends all elements to the given *collection*
 */
public fun <C : MutableCollection<in Char>> String.toCollection(collection: C): C {
    for (item in this) {
        collection.add(item)
    }
    return collection
}

/**
 * Returns a HashSet of all elements
 */
public fun <T> Array<out T>.toHashSet(): HashSet<T> {
    return toCollection(HashSet<T>(mapCapacity(size())))
}

/**
 * Returns a HashSet of all elements
 */
public fun BooleanArray.toHashSet(): HashSet<Boolean> {
    return toCollection(HashSet<Boolean>(mapCapacity(size())))
}

/**
 * Returns a HashSet of all elements
 */
public fun ByteArray.toHashSet(): HashSet<Byte> {
    return toCollection(HashSet<Byte>(mapCapacity(size())))
}

/**
 * Returns a HashSet of all elements
 */
public fun CharArray.toHashSet(): HashSet<Char> {
    return toCollection(HashSet<Char>(mapCapacity(size())))
}

/**
 * Returns a HashSet of all elements
 */
public fun DoubleArray.toHashSet(): HashSet<Double> {
    return toCollection(HashSet<Double>(mapCapacity(size())))
}

/**
 * Returns a HashSet of all elements
 */
public fun FloatArray.toHashSet(): HashSet<Float> {
    return toCollection(HashSet<Float>(mapCapacity(size())))
}

/**
 * Returns a HashSet of all elements
 */
public fun IntArray.toHashSet(): HashSet<Int> {
    return toCollection(HashSet<Int>(mapCapacity(size())))
}

/**
 * Returns a HashSet of all elements
 */
public fun LongArray.toHashSet(): HashSet<Long> {
    return toCollection(HashSet<Long>(mapCapacity(size())))
}

/**
 * Returns a HashSet of all elements
 */
public fun ShortArray.toHashSet(): HashSet<Short> {
    return toCollection(HashSet<Short>(mapCapacity(size())))
}

/**
 * Returns a HashSet of all elements
 */
public fun <T> Iterable<T>.toHashSet(): HashSet<T> {
    return toCollection(HashSet<T>(mapCapacity(collectionSizeOrDefault(12))))
}

/**
 * Returns a HashSet of all elements
 */
public fun <T> Sequence<T>.toHashSet(): HashSet<T> {
    return toCollection(HashSet<T>())
}


deprecated("Migrate to using Sequence<T> and respective functions")
/**
 * Returns a HashSet of all elements
 */
public fun <T> Stream<T>.toHashSet(): HashSet<T> {
    return toCollection(HashSet<T>())
}

/**
 * Returns a HashSet of all elements
 */
public fun String.toHashSet(): HashSet<Char> {
    return toCollection(HashSet<Char>(mapCapacity(length())))
}

/**
 * Returns a LinkedList containing all elements
 */
public fun <T> Array<out T>.toLinkedList(): LinkedList<T> {
    return toCollection(LinkedList<T>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun BooleanArray.toLinkedList(): LinkedList<Boolean> {
    return toCollection(LinkedList<Boolean>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun ByteArray.toLinkedList(): LinkedList<Byte> {
    return toCollection(LinkedList<Byte>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun CharArray.toLinkedList(): LinkedList<Char> {
    return toCollection(LinkedList<Char>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun DoubleArray.toLinkedList(): LinkedList<Double> {
    return toCollection(LinkedList<Double>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun FloatArray.toLinkedList(): LinkedList<Float> {
    return toCollection(LinkedList<Float>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun IntArray.toLinkedList(): LinkedList<Int> {
    return toCollection(LinkedList<Int>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun LongArray.toLinkedList(): LinkedList<Long> {
    return toCollection(LinkedList<Long>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun ShortArray.toLinkedList(): LinkedList<Short> {
    return toCollection(LinkedList<Short>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun <T> Iterable<T>.toLinkedList(): LinkedList<T> {
    return toCollection(LinkedList<T>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun <T> Sequence<T>.toLinkedList(): LinkedList<T> {
    return toCollection(LinkedList<T>())
}


deprecated("Migrate to using Sequence<T> and respective functions")
/**
 * Returns a LinkedList containing all elements
 */
public fun <T> Stream<T>.toLinkedList(): LinkedList<T> {
    return toCollection(LinkedList<T>())
}

/**
 * Returns a LinkedList containing all elements
 */
public fun String.toLinkedList(): LinkedList<Char> {
    return toCollection(LinkedList<Char>())
}

/**
 * Returns a List containing all key-value pairs
 */
public fun <K, V> Map<K, V>.toList(): List<Pair<K, V>> {
    val result = ArrayList<Pair<K, V>>(size())
    for (item in this)
        result.add(item.key to item.value)
    return result
}

/**
 * Returns a List containing all elements
 */
public fun <T> Array<out T>.toList(): List<T> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun BooleanArray.toList(): List<Boolean> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun ByteArray.toList(): List<Byte> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun CharArray.toList(): List<Char> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun DoubleArray.toList(): List<Double> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun FloatArray.toList(): List<Float> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun IntArray.toList(): List<Int> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun LongArray.toList(): List<Long> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun ShortArray.toList(): List<Short> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun <T> Iterable<T>.toList(): List<T> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun <T> Sequence<T>.toList(): List<T> {
    return this.toArrayList()
}


deprecated("Migrate to using Sequence<T> and respective functions")
/**
 * Returns a List containing all elements
 */
public fun <T> Stream<T>.toList(): List<T> {
    return this.toArrayList()
}

/**
 * Returns a List containing all elements
 */
public fun String.toList(): List<Char> {
    return this.toArrayList()
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <T, K> Array<out T>.toMap(selector: (T) -> K): Map<K, T> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, T>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <K> BooleanArray.toMap(selector: (Boolean) -> K): Map<K, Boolean> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, Boolean>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <K> ByteArray.toMap(selector: (Byte) -> K): Map<K, Byte> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, Byte>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <K> CharArray.toMap(selector: (Char) -> K): Map<K, Char> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, Char>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <K> DoubleArray.toMap(selector: (Double) -> K): Map<K, Double> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, Double>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <K> FloatArray.toMap(selector: (Float) -> K): Map<K, Float> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, Float>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <K> IntArray.toMap(selector: (Int) -> K): Map<K, Int> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, Int>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <K> LongArray.toMap(selector: (Long) -> K): Map<K, Long> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, Long>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <K> ShortArray.toMap(selector: (Short) -> K): Map<K, Short> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, Short>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <T, K> Iterable<T>.toMap(selector: (T) -> K): Map<K, T> {
    val capacity = (collectionSizeOrDefault(10)/.75f) + 1
    val result = LinkedHashMap<K, T>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <T, K> Sequence<T>.toMap(selector: (T) -> K): Map<K, T> {
    val result = LinkedHashMap<K, T>()
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}


deprecated("Migrate to using Sequence<T> and respective functions")
/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <T, K> Stream<T>.toMap(selector: (T) -> K): Map<K, T> {
    val result = LinkedHashMap<K, T>()
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values from the given collection indexed by *selector*
 */
public inline fun <K> String.toMap(selector: (Char) -> K): Map<K, Char> {
    val capacity = (length()/.75f) + 1
    val result = LinkedHashMap<K, Char>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), element)
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <T, K, V> Array<out T>.toMap(selector: (T) -> K, transform: (T) -> V): Map<K, V> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <K, V> BooleanArray.toMap(selector: (Boolean) -> K, transform: (Boolean) -> V): Map<K, V> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <K, V> ByteArray.toMap(selector: (Byte) -> K, transform: (Byte) -> V): Map<K, V> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <K, V> CharArray.toMap(selector: (Char) -> K, transform: (Char) -> V): Map<K, V> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <K, V> DoubleArray.toMap(selector: (Double) -> K, transform: (Double) -> V): Map<K, V> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <K, V> FloatArray.toMap(selector: (Float) -> K, transform: (Float) -> V): Map<K, V> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <K, V> IntArray.toMap(selector: (Int) -> K, transform: (Int) -> V): Map<K, V> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <K, V> LongArray.toMap(selector: (Long) -> K, transform: (Long) -> V): Map<K, V> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <K, V> ShortArray.toMap(selector: (Short) -> K, transform: (Short) -> V): Map<K, V> {
    val capacity = (size()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <T, K, V> Iterable<T>.toMap(selector: (T) -> K, transform: (T) -> V): Map<K, V> {
    val capacity = (collectionSizeOrDefault(10)/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <T, K, V> Sequence<T>.toMap(selector: (T) -> K, transform: (T) -> V): Map<K, V> {
    val result = LinkedHashMap<K, V>()
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}


deprecated("Migrate to using Sequence<T> and respective functions")
/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <T, K, V> Stream<T>.toMap(selector: (T) -> K, transform: (T) -> V): Map<K, V> {
    val result = LinkedHashMap<K, V>()
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns Map containing all the values provided by *transform* and indexed by *selector* from the given collection
 */
public inline fun <K, V> String.toMap(selector: (Char) -> K, transform: (Char) -> V): Map<K, V> {
    val capacity = (length()/.75f) + 1
    val result = LinkedHashMap<K, V>(Math.max(capacity.toInt(), 16))
    for (element in this) {
        result.put(selector(element), transform(element))
    }
    return result
}

/**
 * Returns a Set of all elements
 */
public fun <T> Array<out T>.toSet(): Set<T> {
    return toCollection(LinkedHashSet<T>(mapCapacity(size())))
}

/**
 * Returns a Set of all elements
 */
public fun BooleanArray.toSet(): Set<Boolean> {
    return toCollection(LinkedHashSet<Boolean>(mapCapacity(size())))
}

/**
 * Returns a Set of all elements
 */
public fun ByteArray.toSet(): Set<Byte> {
    return toCollection(LinkedHashSet<Byte>(mapCapacity(size())))
}

/**
 * Returns a Set of all elements
 */
public fun CharArray.toSet(): Set<Char> {
    return toCollection(LinkedHashSet<Char>(mapCapacity(size())))
}

/**
 * Returns a Set of all elements
 */
public fun DoubleArray.toSet(): Set<Double> {
    return toCollection(LinkedHashSet<Double>(mapCapacity(size())))
}

/**
 * Returns a Set of all elements
 */
public fun FloatArray.toSet(): Set<Float> {
    return toCollection(LinkedHashSet<Float>(mapCapacity(size())))
}

/**
 * Returns a Set of all elements
 */
public fun IntArray.toSet(): Set<Int> {
    return toCollection(LinkedHashSet<Int>(mapCapacity(size())))
}

/**
 * Returns a Set of all elements
 */
public fun LongArray.toSet(): Set<Long> {
    return toCollection(LinkedHashSet<Long>(mapCapacity(size())))
}

/**
 * Returns a Set of all elements
 */
public fun ShortArray.toSet(): Set<Short> {
    return toCollection(LinkedHashSet<Short>(mapCapacity(size())))
}

/**
 * Returns a Set of all elements
 */
public fun <T> Iterable<T>.toSet(): Set<T> {
    return toCollection(LinkedHashSet<T>(mapCapacity(collectionSizeOrDefault(12))))
}

/**
 * Returns a Set of all elements
 */
public fun <T> Sequence<T>.toSet(): Set<T> {
    return toCollection(LinkedHashSet<T>())
}


deprecated("Migrate to using Sequence<T> and respective functions")
/**
 * Returns a Set of all elements
 */
public fun <T> Stream<T>.toSet(): Set<T> {
    return toCollection(LinkedHashSet<T>())
}

/**
 * Returns a Set of all elements
 */
public fun String.toSet(): Set<Char> {
    return toCollection(LinkedHashSet<Char>(mapCapacity(length())))
}

/**
 * Returns a SortedSet of all elements
 */
public fun <T> Array<out T>.toSortedSet(): SortedSet<T> {
    return toCollection(TreeSet<T>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun BooleanArray.toSortedSet(): SortedSet<Boolean> {
    return toCollection(TreeSet<Boolean>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun ByteArray.toSortedSet(): SortedSet<Byte> {
    return toCollection(TreeSet<Byte>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun CharArray.toSortedSet(): SortedSet<Char> {
    return toCollection(TreeSet<Char>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun DoubleArray.toSortedSet(): SortedSet<Double> {
    return toCollection(TreeSet<Double>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun FloatArray.toSortedSet(): SortedSet<Float> {
    return toCollection(TreeSet<Float>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun IntArray.toSortedSet(): SortedSet<Int> {
    return toCollection(TreeSet<Int>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun LongArray.toSortedSet(): SortedSet<Long> {
    return toCollection(TreeSet<Long>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun ShortArray.toSortedSet(): SortedSet<Short> {
    return toCollection(TreeSet<Short>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun <T> Iterable<T>.toSortedSet(): SortedSet<T> {
    return toCollection(TreeSet<T>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun <T> Sequence<T>.toSortedSet(): SortedSet<T> {
    return toCollection(TreeSet<T>())
}


deprecated("Migrate to using Sequence<T> and respective functions")
/**
 * Returns a SortedSet of all elements
 */
public fun <T> Stream<T>.toSortedSet(): SortedSet<T> {
    return toCollection(TreeSet<T>())
}

/**
 * Returns a SortedSet of all elements
 */
public fun String.toSortedSet(): SortedSet<Char> {
    return toCollection(TreeSet<Char>())
}

