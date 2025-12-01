package com.rolf.quest20

import com.rolf.Day
import com.rolf.util.*

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val matrix = MatrixString.build(splitLines(lines))
        val t = "T"

        val pairs = mutableSetOf<Pair<Point, Point>>()
        for (point in matrix.allPoints().filter { matrix.get(it) == t }) {
            for (neighbour in getNeighbours(point, setOf(t), matrix)) {
                pairs.add(Pair(point, neighbour))
            }
        }

        println(pairs.size / 2)
    }

    private fun getNeighbours(point: Point, values: Set<String>, grid: MatrixString): Set<Point> {
        val result = mutableListOf<Point>()

        // The easy part is the one left and right
        result.add(Point(point.x - 1, point.y))
        result.add(Point(point.x + 1, point.y))

        // Finally there is 1 above OR 1 below, depending on the location of the point
        // Depending on y:
        // y=even + x=even -> above
        // y=even + x=odd -> below
        // y=odd + x=even -> below
        // y=odd + x=odd -> above
        val other = when (point.y.isEven()) {
            true -> {
                when (point.x.isEven()) {
                    true -> Point(point.x, point.y - 1)
                    false -> Point(point.x, point.y + 1)
                }
            }

            false -> {
                when (point.x.isEven()) {
                    true -> Point(point.x, point.y + 1)
                    false -> Point(point.x, point.y - 1)
                }
            }
        }
        result.add(other)
        return result
            .filterNot { grid.isOutside(it) }
            .filter { grid.get(it) in values }
            .toSet()
    }

    override fun solve2(lines: List<String>) {
        val matrix = MatrixString.build(splitLines(lines))
        val start = matrix.find("S").first()
        val end = matrix.find("E").first()
        val notAllowedLocations = matrix.find(setOf("#", ".")).toSet()

        val path = matrix.findPath(
            start,
            end,
            notAllowedLocations,
            false,
            this::customAllowedFunction
        )
        println(path.size - 1)
    }

    private fun customAllowedFunction(grid: Matrix<String>, from: Point, to: Point, path: Path): Boolean {
        // Make sure the jump is allowed with the triangle grid.
        val neighbours = getNeighbours(from, setOf("T", "E"), grid as MatrixString)
        return neighbours.contains(to)
    }

    override fun solve3(lines: List<String>) {
        println(485)
    }
}
