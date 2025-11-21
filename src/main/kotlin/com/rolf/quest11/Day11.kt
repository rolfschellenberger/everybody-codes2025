package com.rolf.quest11

import com.rolf.Day

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        var columns = lines.map { it.toLong() }

        var rounds = 10
        while (rounds > 0) {
            val (updated1, list1) = firstPhase(columns)
            columns = list1
            if (!updated1) break
            rounds--
        }

        while (rounds > 0) {
            val (updated2, list2) = secondPhase(columns)
            columns = list2
            if (!updated2) break
            rounds--
        }

        println(checkSum(columns))
    }

    private fun checkSum(columns: List<Long>): Long {
        return columns.mapIndexed { index, value ->
            (index + 1L) * value
        }.sum()
    }

    private fun firstPhase(columns: List<Long>): Pair<Boolean, List<Long>> {
        val list = columns.toMutableList()
        var updated = false
        for (i in 0 until list.size - 1) {
            val a = list[i]
            val b = list[i + 1]

            if (b < a) {
                list[i] = a - 1
                list[i + 1] = b + 1
                updated = true
            }
        }
        return updated to list
    }

    private fun secondPhase(columns: List<Long>): Pair<Boolean, List<Long>> {
        val list = columns.toMutableList()
        var updated = false
        for (i in 0 until list.size - 1) {
            val a = list[i]
            val b = list[i + 1]

            if (b > a) {
                list[i] = a + 1
                list[i + 1] = b - 1
                updated = true
            }
        }
        return updated to list
    }

    override fun solve2(lines: List<String>) {
        var columns = lines.map { it.toLong() }

        var rounds = 0
        while (true) {
            val (updated1, list1) = firstPhase(columns)
            if (!updated1) break
            columns = list1
            rounds++
        }

        while (true) {
            val (updated2, list2) = secondPhase(columns)
            if (!updated2) break
            columns = list2
            rounds++
        }

        println(rounds)
    }

    override fun solve3(lines: List<String>) {
        val columns = lines.map { it.toLong() }

        // Already sorted
        if (columns.sorted() != columns) {
            throw IllegalStateException("Input is not sorted properly.")
        }

        // To optimize for this, we need to know what the value will be per column
        val average = columns.sum() / columns.size

        // Before we can establish how many moves it would take to distribute.
        var rounds = 0L
        for (i in 0 until columns.size - 1) {
            val a = columns[i]
            if (a < average) {
                rounds += average - a
            }
        }
        println(rounds)
    }
}
