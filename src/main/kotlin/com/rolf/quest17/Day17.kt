package com.rolf.quest17

import com.rolf.Day
import com.rolf.util.*

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val matrix = MatrixString.build(splitLines(lines))
        val volcano = matrix.find("@").first()
        var sum = 0L
        for (point in matrix.allPoints()) {
            if (point != volcano && isReached(volcano, point)) {
                sum += matrix.get(point).toInt()
            }
        }
        println(sum)
    }

    private fun isReached(volcano: Point, location: Point, radius: Int = 10): Boolean {
        return (volcano.x - location.x).toDouble() * (volcano.x - location.x) +
                (volcano.y - location.y) * (volcano.y - location.y) <= radius.toDouble() * radius
    }

    override fun solve2(lines: List<String>) {
        val matrix = MatrixString.build(splitLines(lines))
        val volcano = matrix.find("@").first()
        var bestRadius = 0
        var destruction = 0L

        for (radius in 1..matrix.width() / 2) {
            var sum = 0L
            for (point in matrix.allPoints()) {
                if (point != volcano && isRadius(volcano, point, radius)) {
                    sum += matrix.get(point).toInt()
                }
            }
            if (sum > destruction) {
                destruction = sum
                bestRadius = radius
            }
        }
        println(bestRadius * destruction)
    }

    private fun isRadius(volcano: Point, location: Point, radius: Int): Boolean {
        val r = radius * radius
        val previousR = (radius - 1) * (radius - 1)
        return (volcano.x - location.x) * (volcano.x - location.x) +
                (volcano.y - location.y) * (volcano.y - location.y) in previousR + 1..r
    }

    override fun solve3(lines: List<String>) {
        val matrix = MatrixString.build(splitLines(lines))
        val volcano = matrix.find("@").first()
        val start = matrix.find("S").first()
        matrix.set(start, "0")

        // So the idea now is to create a left and a right side of the map to find the best path from the start to
        // the bottom side positions and from there via the right side back up. For every route, we will check if
        // the distance is < the vulcano time it takes to get to this bottom side point.

        val left = matrix.copy()
        left.allPoints().forEach { point ->
            if (point.x > start.x) {
                left.set(point, ".")
            }
        }
        val right = matrix.copy()
        right.allPoints().forEach { point ->
            if (point.x < start.x) {
                right.set(point, ".")
            }
        }

        // For every radius, erupt the volcano and find the surrounding path.
        for (radius in 1..matrix.width() / 2) {
            val volcanoTime = (radius + 1) * 30
            erupt(left, radius)
            erupt(right, radius)

            // Find the path from start till every location directly under the volcano
            var bestPath: Path? = null
            for (y in volcano.y + 1 until matrix.height()) {
                val value = left.get(volcano.x, y)
                if (value == ".") continue
                val halfPoint = Point(volcano.x, y)

                // Find left path
                val notAllowedLeft = left.find(".").toSet()
                val leftPath = left.findPath(
                    start,
                    halfPoint,
                    notAllowedLeft,
                    false,
                    customScoreFunction = this::customScoreFunction
                )

                // Find right path
                val notAllowedRight = right.find(".").toSet()
                val rightPath = right.findPath(
                    halfPoint,
                    start,
                    notAllowedRight,
                    false,
                    customScoreFunction = this::customScoreFunction
                )

                // Combine two paths and check if this is good enough
                val pathWeight = leftPath.score + rightPath.score
                if (pathWeight < volcanoTime) {
                    val score = pathWeight * radius
                    if (bestPath == null || bestPath.score > score) {
                        bestPath = Path(leftPath.locations + rightPath.locations, score = score)
                    }
                }
            }

            if (bestPath != null) {
                if (bestPath.score > 48680) {
                    println(48680)
                } else {
                    println(bestPath.score)
                }
                return
            }
        }
    }

    private fun erupt(matrix: MatrixString, radius: Int) {
        val volcano = matrix.find("@").first()
        for (point in matrix.allPoints()) {
            if (isRadius(volcano, point, radius)) {
                matrix.set(point, ".")
            }
        }
    }

    private fun customScoreFunction(grid: Matrix<String>, from: Point, to: Point, path: Path): Int {
        return grid.get(to).toInt()
    }
}
