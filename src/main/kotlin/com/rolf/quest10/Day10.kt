package com.rolf.quest10

import com.rolf.Day
import com.rolf.util.MatrixString
import com.rolf.util.Point
import com.rolf.util.splitLines

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val sheepMatrix = MatrixString.build(splitLines(lines))
        val steps = if (sheepMatrix.width() > 13) 4 else 3
        val dragonValue = "D"
        val sheepValue = "S"

        val dragonMatrix = MatrixString.buildDefault(sheepMatrix.width(), sheepMatrix.height(), ".")
        val dragon = dragonMatrix.center()
        dragonMatrix.set(dragon, dragonValue)

        repeat(steps) {
            val dragons = dragonMatrix.find(dragonValue)
            for (dragon in dragons) {
                val steps = getDragonSteps(dragon, dragonMatrix)
                for (step in steps) {
                    dragonMatrix.set(step, dragonValue)
                }
            }
        }
        val dragons = dragonMatrix.find(dragonValue).toSet()

        var count = 0
        for (point in sheepMatrix.find(sheepValue)) {
            if (dragons.contains(point)) {
                count++
            }
        }
        println(count)
    }

    private fun getDragonSteps(dragon: Point, matrix: MatrixString): Set<Point> {
        val steps = listOf(
            Point(dragon.x - 2, dragon.y - 1),
            Point(dragon.x + 2, dragon.y - 1),
            Point(dragon.x - 1, dragon.y - 2),
            Point(dragon.x + 1, dragon.y - 2),
            Point(dragon.x - 2, dragon.y + 1),
            Point(dragon.x + 2, dragon.y + 1),
            Point(dragon.x - 1, dragon.y + 2),
            Point(dragon.x + 1, dragon.y + 2)
        )
        return steps.filterNot {
            matrix.isOutside(it)
        }.toSet()
    }

    private fun getSheepMoves(
        dragon: Point,
        sheep: Set<Point>,
        hideOuts: Set<Point>,
    ): Set<Set<Point>> {
        val moves = mutableSetOf<Set<Point>>()
        for (s in sheep) {
            val step = Point(s.x, s.y + 1)

            // The move is valid, when the sheep is moving to an open space or a space with a hiding spot
            // When the sheep moves to the dragon without a hide-out, it is not valid
            if (step == dragon && step !in hideOuts) continue

            // Add the new sheep locations where the sheep moved into 'step'
            moves.add(sheep - s + step)
        }

        // If there are no possible moves, keep the original state of sheep
        if (moves.isEmpty()) {
            moves.add(sheep)
        }
        return moves
    }

    override fun solve2(lines: List<String>) {
        val sheepMatrix = MatrixString.build(splitLines(lines))
        val hideMatrix = sheepMatrix.copy()
        sheepMatrix.replace(mapOf("#" to "."))
        hideMatrix.replace(mapOf("S" to "."))
        val dragonMatrix = MatrixString.buildDefault(sheepMatrix.width(), sheepMatrix.height(), ".")
        dragonMatrix.set(dragonMatrix.center(), "D")
        val steps = if (sheepMatrix.width() > 13) 20 else 3

        var kills = 0
        repeat(steps) {
            moveDragons(dragonMatrix)
            val killed1 = killSheep(dragonMatrix, sheepMatrix, hideMatrix)
            moveSheep(sheepMatrix)
            val killed2 = killSheep(dragonMatrix, sheepMatrix, hideMatrix)
            kills += killed1 + killed2
        }
        println(kills)
    }

    private fun moveDragons(dragonMatrix: MatrixString) {
        val dragons = dragonMatrix.find("D")
        dragonMatrix.replace(mapOf("D" to "."))
        for (dragon in dragons) {
            val steps = getDragonSteps(dragon, dragonMatrix)
            for (step in steps) {
                dragonMatrix.set(step, "D")
            }
        }
    }

    private fun moveSheep(sheepMatrix: MatrixString) {
        val sheep = sheepMatrix.find("S")
        sheepMatrix.replace(mapOf("S" to "."))
        for (s in sheep) {
            val newPosition = sheepMatrix.getDown(s)
            if (newPosition != null) {
                sheepMatrix.set(newPosition, "S")
            }
        }
    }

    private fun killSheep(
        dragonMatrix: MatrixString,
        sheepMatrix: MatrixString,
        hideMatrix: MatrixString,
    ): Int {
        var killed = 0
        val dragons = dragonMatrix.find("D").toSet()
        val sheep = sheepMatrix.find("S")
        val hideouts = hideMatrix.find("#").toSet()
        for (s in sheep) {
            if (dragons.contains(s) && !hideouts.contains(s)) {
                sheepMatrix.set(s, ".")
                killed++
            }
        }
        return killed
    }

    override fun solve3(lines: List<String>) {
        val sheepMatrix = MatrixString.build(splitLines(lines))
        val matrix = sheepMatrix.copy()
        matrix.allPoints().forEach { matrix.set(it, ".") }

        val dragon = sheepMatrix.find("D").first()
        val sheep = sheepMatrix.find("S").toSet()
        val hideOuts = sheepMatrix.find("#").toSet()

        val sequences = findSequences(matrix, dragon, sheep, hideOuts)
        println(sequences)
    }

    private fun findSequences(
        matrix: MatrixString,
        dragon: Point,
        sheep: Set<Point>,
        hideOuts: Set<Point>,
        cache: MutableMap<Pair<Point, Set<Point>>, Long> = mutableMapOf(),
    ): Long {
        // If we already visited this configuration, return the result.
        val key = dragon to sheep
        if (cache.contains(key)) return cache.getValue(key)

        // If all sheep are dead, we are done!
        if (sheep.isEmpty()) return 1

        // If a sheep escapes, this is not a valid sequence, and we don't need to continue
        if (sheep.any { matrix.isOutside(it) }) return 0

        // Get all possible sheep configurations
        // Since sheep never move into the dragon, there is no need to check for kills.
        val sheepMoves = getSheepMoves(dragon, sheep, hideOuts)

        // Get all dragon steps
        val dragonSteps = getDragonSteps(dragon, matrix)

        // Now inspect all new possible combinations
        var sequences = 0L
        for (sheepMove in sheepMoves) {
            for (dragonStep in dragonSteps) {
                // Kill a sheep only when not in a hide-out
                val sheepLeft = if (dragonStep in hideOuts) sheepMove else sheepMove - dragonStep
                sequences += findSequences(matrix, dragonStep, sheepLeft, hideOuts, cache)
            }
        }
        cache[key] = sequences
        return sequences
    }
}
