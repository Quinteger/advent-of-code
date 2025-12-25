package dev.quinteger.aoc.util

fun <T> List<T>.previousWrappingAround(currentIndex: Int): T {
    return if (currentIndex == 0) {
        last()
    } else {
        this[currentIndex - 1]
    }
}

fun <T> List<T>.nextWrappingAround(currentIndex: Int): T {
    return if (currentIndex == lastIndex) {
        first()
    } else {
        this[currentIndex + 1]
    }
}

fun <T> List<CharSequence>.splitLinesIntoTwoIntegers(separator: Char, action: (Long, Long) -> T): List<T> =
    asSequence()
        .map { it.split(separator) }
        .map { action(it[0].toLong(), it[1].toLong()) }
        .toList()

fun <T> List<CharSequence>.splitLinesIntoThreeIntegers(separator: Char, action: (Long, Long, Long) -> T): List<T> =
    asSequence()
        .map { it.split(separator) }
        .map { action(it[0].toLong(), it[1].toLong(), it[2].toLong()) }
        .toList()