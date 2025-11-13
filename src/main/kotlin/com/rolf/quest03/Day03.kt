package com.rolf.quest03

import com.rolf.Day
import com.rolf.util.splitLine

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val numbers = splitLine(lines[0], ",")
            .map { it.toInt() }
            .toSet()
            .sorted()
            .reversed()
        println(numbers.sum())
    }

    override fun solve2(lines: List<String>) {
        val numbers = splitLine(lines[0], ",")
            .map { it.toInt() }
            .toSet()
            .sorted()
            .take(20)
        println(numbers.sum())
    }

    override fun solve3(lines: List<String>) {
        val numbers = splitLine(lines[0], ",")
        val counts = numbers.groupingBy { it }.eachCount()
        println(counts.maxBy { it.value }.value)
    }
}
