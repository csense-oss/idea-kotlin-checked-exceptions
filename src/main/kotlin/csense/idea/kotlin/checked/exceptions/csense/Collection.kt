package csense.idea.kotlin.checked.exceptions.csense

import csense.kotlin.extensions.collections.*


public inline fun <Item, U> Collection<Item>.selectLastOrNull(mappingPredicate: (Item) -> U?): U? {
    forEachBackwards { item: Item ->
        mappingPredicate(item)?.let { mapped: U ->
            return@selectLastOrNull mapped
        }
    }
    return null
}