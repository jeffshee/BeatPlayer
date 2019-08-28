package com.crrl.beatplayer.extensions

fun <T> List<T>?.moveElement(fromIndex: Int, toIndex: Int): List<T> {
    if (this == null) {
        return emptyList()
    }
    return toMutableList().apply {
        val deleted = removeAt(fromIndex)
        add(toIndex, deleted)
    }
}