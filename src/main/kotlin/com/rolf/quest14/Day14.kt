package com.rolf.quest14

import com.rolf.Day
import com.rolf.util.*
import kotlin.math.ceil

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        var matrix = MatrixString.build(splitLines(lines))
        var sum = 0
        repeat(10) {
            matrix = step(matrix)
            sum += matrix.count("#")
        }
        println(sum)
    }

    private fun step(matrix: MatrixString): MatrixString {
        val copy = MatrixString.buildDefault(matrix.width(), matrix.height(), ".")
        for (point in matrix.allPoints()) {
            when (isActive(point, matrix)) {
                true -> copy.set(point, "#")
                false -> copy.set(point, ".")
            }
        }
        return copy
    }

    private fun isActive(point: Point, matrix: MatrixString): Boolean {
        val active = matrix.get(point) == "#"
        val activeNeighbours = matrix.getNeighbours(point, horizontal = false, vertical = false, diagonal = true)
            .filter {
                matrix.get(it) == "#"
            }

        return when (active) {
            true -> activeNeighbours.size.isOdd()
            false -> activeNeighbours.size.isEven()
        }
    }

    override fun solve2(lines: List<String>) {
        var matrix = MatrixString.build(splitLines(lines))
        var sum = 0
        repeat(2025) {
            matrix = step(matrix)
            sum += matrix.count("#")
        }
        println(sum)
    }

    override fun solve3(lines: List<String>) {
        val pattern = MatrixString.build(splitLines(lines))
        var matrix = MatrixString.buildDefault(34, 34, ".")

        val patternToRound = mutableMapOf<MatrixString, Int>()
        var rounds = 0
        while (true) {
            rounds++
            matrix = step(matrix)
            if (matchPattern(matrix, pattern)) {
                if (!patternToRound.containsKey(matrix)) {
                    patternToRound[matrix] = rounds
                } else {
                    break
                }
            }
        }

        // Now we know all patterns and we can calculate how often they occur
        val offset = patternToRound.values.min().toDouble()
        val totalRounds = 1_000_000_000
        var sum = 0L
        for ((matrix, round) in patternToRound) {
            val occurrences = ceil((totalRounds - round) / (rounds - offset))
            val active = matrix.count("#")
            sum += active * occurrences.toInt()
        }
        println(sum)
    }

    private fun matchPattern(matrix: MatrixString, pattern: MatrixString): Boolean {
        val left = (matrix.width() / 2) - (pattern.width() / 2)
        val right = (matrix.width() / 2) + (pattern.width() / 2) - 1
        val top = (matrix.height() / 2) - (pattern.height() / 2)
        val bottom = (matrix.height() / 2) + (pattern.height() / 2) - 1
        val cutout = matrix.copy()
        cutout.cutOut(Point(left, top), Point(right, bottom))
        return cutout.allElements() == pattern.allElements()
    }
}
