package com.rolf.quest12

import com.rolf.Day
import com.rolf.util.*

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val matrix = MatrixString.build(splitLines(lines))
        val water = waterFill(matrix, setOf(matrix.topLeft()))
        println(water.size)
    }

    fun waterFill(
        grid: Matrix<String>,
        start: Set<Point>,
    ): Set<Point> {
        val watered = start.toMutableSet()
        val inspect = start.toMutableSet()
        while (inspect.isNotEmpty()) {
            val newInspect = mutableSetOf<Point>()
            for (location in inspect) {
                if (!grid.get(location).isNumeric()) continue
                val value = grid.get(location).toInt()
                val neighbours =
                    grid.getNeighbours(location, diagonal = false) - watered
                val validNeighbours = neighbours.filterNot {
                    it in watered
                }.filter {
                    grid.get(it).isNumeric() && grid.get(it).toInt() <= value
                }
                watered += validNeighbours
                newInspect += validNeighbours
            }

            inspect.clear()
            inspect.addAll(newInspect)
        }
        return watered
    }

    override fun solve2(lines: List<String>) {
        val matrix = MatrixString.build(splitLines(lines))
        val water = waterFill(matrix, setOf(matrix.topLeft(), matrix.bottomRight()))
        println(water.size)
    }

    override fun solve3(lines: List<String>) {
        val original = MatrixString.build(splitLines(lines))
        val matrix = MatrixString.build(splitLines(lines))
        val (barrel1, barrels1) = findMaximumBarrels(matrix)
        barrels1.forEach {
            matrix.set(it, " ")
        }
        val (barrel2, barrels2) = findMaximumBarrels(matrix)
        barrels2.forEach {
            matrix.set(it, " ")
        }
        val (barrel3, barrels3) = findMaximumBarrels(matrix)
        barrels3.forEach {
            matrix.set(it, " ")
        }

        val water = waterFill(original, setOf(barrel1, barrel2, barrel3))
        println(water.size)
    }

    fun findMaximumBarrels(matrix: Matrix<String>): Pair<Point, Set<Point>> {
        var barrel = Point(0, 0)
        var barrels = setOf<Point>()
        var mostBarrels = 0
        for (point in matrix.allPoints()) {
            val water = waterFill(matrix, setOf(point))
            if (water.size > mostBarrels) {
                barrel = point
                barrels = water
                mostBarrels = water.size
            }
        }
        return barrel to barrels
    }
}
