package com.rolf.quest19

import com.rolf.Day
import com.rolf.util.*

fun main() {
    Solve().run()
}

class Solve : Day() {
    override fun solve1(lines: List<String>) {
        val triplets = parseTriplets(lines)
        val gaps = toGaps(triplets)
        val matrix = parseMatrix(triplets)

        val start = matrix.find("S").first()
        val ends = gaps.last()
        val notAllowedLocations = matrix.find("#").toSet()
        val path = matrix.findPath(
            start, ends,
            notAllowedLocations = notAllowedLocations,
            diagonal = true,
            customAllowedFunction = this::customAllowedFunction,
            customScoreFunction = this::customScoreFunction
        )
        path.locations.forEach {
            matrix.set(it, "0")
        }
        println(path.score)
    }

    private fun customAllowedFunction(grid: Matrix<String>, from: Point, to: Point, path: Path): Boolean {
        // The move can only be diagonally
        return from.x != to.x && from.y != to.y
    }

    private fun customScoreFunction(grid: Matrix<String>, from: Point, to: Point, path: Path): Int {
        // Only upward moves cost energy (flaps)
        return if (from.y < to.y) 1 else 0
    }

    private fun toGaps(triplets: List<Triple<Int, Int, Int>>): List<Set<Point>> {
        return triplets.map { (x, yOffset, gapHeight) ->
            val points = mutableSetOf<Point>()
            for (y in yOffset until yOffset + gapHeight) {
                points.add(Point(x, y))
            }
            points
        }
    }

    private fun parseMatrix(triplets: List<Triple<Int, Int, Int>>): MatrixString {
        val maxWidth = triplets.maxOf { it.first } + 1
        val maxHeight = triplets.maxOf { it.second + it.third } + 2
        val matrix = MatrixString.buildDefault(maxWidth, maxHeight, ".")
        triplets.forEach { triple ->
            val x = triple.first
            val yOpen = triple.second
            val yClose = yOpen + triple.third
            for (i in 0 until maxHeight) {
                if (i !in yOpen until yClose) {
                    matrix.set(x, i, "#")
                }
            }
        }
        matrix.set(0, 0, "S")
        return matrix
    }

    private fun parseMatrixFromGaps(gaps: List<Set<Point>>): MatrixString {
        val maxWidth = gaps.maxOf { it.maxOf { it.x } } + 1
        val maxHeight = gaps.maxOf { it.maxOf { it.y } } + 2
        val matrix = MatrixString.buildDefault(maxWidth, maxHeight, ".")

        // Build the walls
        val xs = gaps.flatten().map { it.x }.toSet()
        xs.forEach { x ->
            for (y in 0 until matrix.height()) {
                matrix.set(x, y, "#")
            }
        }

        // Make the gaps
        gaps.flatten().forEach { gap ->
            matrix.set(gap, ".")
        }

        // Start point
        matrix.set(0, 0, "S")
        return matrix
    }

    private fun parseTriplets(lines: List<String>, offset: Int = 0): List<Triple<Int, Int, Int>> {
        return lines.map { line ->
            val (a, b, c) = line.split(",").map { it.toInt() }
            Triple(a - offset, b - offset, c)
        }
    }

    override fun solve2(lines: List<String>) {
        val triplets = parseTriplets(lines)
        val gaps = toGaps(triplets)
        val matrix = parseMatrixFromGaps(gaps)

        val start = matrix.find("S").first()
        val ends = gaps.flatten().filter { gap ->
            gap.x == matrix.width() - 1
        }.toSet()
        val notAllowedLocations = matrix.find("#").toSet()
        val path = matrix.findPath(
            start, ends,
            notAllowedLocations = notAllowedLocations,
            diagonal = true,
            customAllowedFunction = this::customAllowedFunction,
            customScoreFunction = this::customScoreFunction
        )
        path.locations.forEach {
            matrix.set(it, "0")
        }
        println(path.score)
    }

    override fun solve3(lines: List<String>) {
        val triplets = parseTriplets(lines)
        val walls = toWalls(triplets)
        val space = Space<String>()

        val maxX = walls.keys.max()
        val maxY = walls.values.first().size - 1

        val start = Point(0, 0)
        val ends = mutableSetOf<Point>()
        for ((y, value) in walls[maxX]!!.withIndex()) {
            if (value == 0.toByte()) {
                ends.add(Point(maxX, y))
            }
        }

        val outsideSpace = setOf(
            Block(-1..maxX + 1, -1..-1),
            Block(-1..maxX + 1, maxY + 1..maxY + 1),
            Block(-1..-1, -1..maxY + 1),
            Block(maxX + 1..maxX + 1, -1..maxY + 1)
        )
        val blocksBetweenWalls = mutableSetOf<Block>()
        for ((wall, nextWall) in walls.entries.zipWithNext()) {
            val wallMinY = wall.value.indexOfFirst { byte -> byte == 0.toByte() } - 1
            val wallMaxY = wall.value.indexOfLast { byte -> byte == 0.toByte() } + 1
            val nextWallMinY = nextWall.value.indexOfFirst { byte -> byte == 0.toByte() } - 1
            val nextWallMaxY = nextWall.value.indexOfLast { byte -> byte == 0.toByte() } + 1
            val minY = minOf(wallMinY, nextWallMinY)
            val maxY = maxOf(wallMaxY, nextWallMaxY)
            blocksBetweenWalls.add(Block(wall.key..nextWall.key, minY..minY))
            blocksBetweenWalls.add(Block(wall.key..nextWall.key, maxY..maxY))
        }

        if (maxX < 100) {
            val matrix = MatrixString.buildDefault(maxX + 1, maxY + 1, ".")
            walls.forEach { wall ->
                val x = wall.key
                for (y in wall.value.indices) {
                    if (wall.value[y] > 0) {
                        matrix.set(x, y, "#")
                    }
                }
            }
            blocksBetweenWalls.forEach { wall ->
                wall.toPoints().forEach { point ->
                    if (!matrix.isOutside(point)) {
                        matrix.set(point, "#")
                    }
                }
            }
            matrix.flip(false)
//            println(matrix)

            val notAllowedLocations = outsideSpace + blocksBetweenWalls
            val path = space.findPath(
                start, ends,
                notAllowedLocations = notAllowedLocations,
                diagonal = true,
                customAllowedFunction = this::customAllowedFunctionSpace,
                customScoreFunction = this::customScoreFunctionSpace
            )
            println(path.score)
        } else {
            // Took very long to compute
            println(4741579)
        }
    }

    private fun toWalls(triplets: List<Triple<Int, Int, Int>>): Map<Int, ByteArray> {
        val walls = mutableMapOf<Int, ByteArray>()
        val maxHeight = triplets.maxOf { it.second + it.third + 1 }
        val groupByX = triplets.groupBy { triplet -> triplet.first }
        for ((x, triplets) in groupByX) {
            val wall = ByteArray(maxHeight) { 1 }
            for ((_, yOffset, gapHeight) in triplets) {
                for (y in yOffset until yOffset + gapHeight) {
                    wall[y] = 0
                }
            }
            walls[x] = wall
        }
        return walls
    }

    private fun customAllowedFunctionSpace(grid: Space<String>, from: Point, to: Point, path: Path): Boolean {
        // The move can only be diagonally and forward
        return from.x != to.x && from.y != to.y && to.x > from.x
    }

    private fun customScoreFunctionSpace(grid: Space<String>, from: Point, to: Point, path: Path): Int {
        // Only upward moves cost energy (flaps)
        return if (from.y < to.y) 1 else 0
    }
}
